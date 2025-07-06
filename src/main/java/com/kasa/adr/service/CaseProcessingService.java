package com.kasa.adr.service;


import com.kasa.adr.dto.CaseUploadRequest;
import com.kasa.adr.model.CaseDetails;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.model.CaseUploadDetails;
import com.kasa.adr.model.User;
import com.kasa.adr.repo.CaseHistoryDetailsRepo;
import com.kasa.adr.repo.CaseRepository;
import com.kasa.adr.repo.CaseUploadDetailsRepo;
import com.kasa.adr.repo.UserRepository;
import com.kasa.adr.service.external.S3Service;
import com.kasa.adr.util.CsvToCaseDetailsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CaseProcessingService {
    Logger logger = LoggerFactory.getLogger(CaseProcessingService.class);
    @Autowired
    CaseUploadDetailsRepo caseUploadDetailsRepo;

    @Autowired
    CaseRepository caseRepository;

    @Autowired
    CaseHistoryDetailsRepo caseHistoryDetailsRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    CsvToCaseDetailsMapper csvToCaseDetailsMapper;
    @Autowired
    S3Service s3Service;

    @Async
    public void processCaseFile(CaseUploadRequest caseUploadRequest) {
        List<User> arbitrators = userRepository.findAllByIds(caseUploadRequest.getArbitrators());
        String claimantAdminId = caseUploadRequest.getClaimantAdminId();
        Optional<User> claimant = userRepository.findById(claimantAdminId);
        Optional<User> uploadedBy = userRepository.findById(caseUploadRequest.getUserId());
        logger.info("claimantAdminId " + claimant + " uploadedBy " + uploadedBy);
        if (claimant.isPresent() && uploadedBy.isPresent()) {
            logger.error("User not found for claimantAdminId: {} or userId: {}", claimantAdminId, caseUploadRequest.getUserId());

            CaseUploadDetails caseUploadDetails = CaseUploadDetails.builder().templateId(caseUploadRequest.getTemplateId()).file(caseUploadRequest.getFileName()).claimantAdmin(claimant.get()).arbitrators(arbitrators).monthYear(caseUploadRequest.getMonthYear()).createdAt(Instant.now()).uploadedBy(uploadedBy.get()).build();
            CaseUploadDetails uploadDetails = caseUploadDetailsRepo.save(caseUploadDetails);
            processCSV(uploadDetails);
        } else {
            logger.error("Claimant Admin or Uploaded By user not found for claimantAdminId: {} or userId: {}", claimantAdminId, caseUploadRequest.getUserId());
        }
    }

    @Async
    protected void processCSV(CaseUploadDetails uploadDetails) {
        logger.info("Started Processing");
        String key = "csv/" + uploadDetails.getUploadedBy().getId() + "/" + uploadDetails.getFile();
        String csvPath = s3Service.localFilePath(key);
        List<CaseDetails> caseDetailsList = null;
        try {
            caseDetailsList = csvToCaseDetailsMapper.mapCsvToCaseDetails(csvPath);

            assignRandomArbitrator(caseDetailsList, uploadDetails);
            List<CaseDetails> cases = caseRepository.saveAll(caseDetailsList);

            cases.stream().forEach(aCase -> {
                CaseHistoryDetails historyDetails= CaseHistoryDetails.builder()
                                .caseId(aCase.getId())
                                .createdAt(Instant.now())
                                .description("Case Created and Arbitrator Assigned - " + aCase.getAssignedArbitrator().getName())
                                .createdBy(aCase.getCreatedBy().getName())

                                .build();
                caseHistoryDetailsRepo.save(historyDetails);
                    }
            );


        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                Files.deleteIfExists(Path.of(csvPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        logger.info("Processing case file: {}", uploadDetails.getFile());
        //Process the case file and create case objects
        //List<Case> caseList = getCaseList(caseUploadRequest);
        //Assign arbitrators to the case
        //Job Trigger

        //send notifications  to arbitrators
        //update casehistory details
        //send first notice in queue - notice pdf, email body, sms body, whats app body
        //update casehistory details
    }

    public static void assignRandomArbitrator(List<CaseDetails> caseDetailsList, CaseUploadDetails caseUploadDetails) {
        if (caseDetailsList == null || caseUploadDetails.getArbitrators() == null || caseUploadDetails.getArbitrators().isEmpty()) {
            return;
        }

        Random random = new Random();

        for (CaseDetails caseDetail : caseDetailsList) {
            User randomUser = caseUploadDetails.getArbitrators().get(random.nextInt(caseUploadDetails.getArbitrators().size()));
            caseDetail.setAssignedArbitrator(randomUser);
            caseDetail.setMonthYear(caseUploadDetails.getMonthYear());
            caseDetail.setCaseUploadDetails(caseUploadDetails.getId());
            caseDetail.setClaimantAdmin(caseUploadDetails.getClaimantAdmin());

        }
    }

    private List<CaseDetails> getCaseList(CaseUploadRequest caseUploadRequest) {
        return new ArrayList<>();
    }

    public List<CaseUploadDetails> myUploads(String userId) {

        return caseUploadDetailsRepo.findAll();
    }
}
