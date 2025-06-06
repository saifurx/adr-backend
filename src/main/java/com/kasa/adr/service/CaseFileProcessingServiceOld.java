//package com.kasa.adr.service;
//
//
//import com.kasa.adr.dto.CaseUploadRequest;
//import com.kasa.adr.model.Case;
//import com.kasa.adr.model.CaseHistory;
//import com.kasa.adr.model.CaseUploadDetails;
//import com.kasa.adr.model.User;
//import com.kasa.adr.repo.CaseRepository;
//import com.kasa.adr.repo.CaseUploadDetailsRepo;
//import com.kasa.adr.repo.TemplateRepo;
//import com.kasa.adr.repo.UserRepository;
//import com.kasa.adr.service.external.S3Service;
//import com.kasa.adr.util.CaseProcessor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//
//@Service
//public class CaseFileProcessingServiceOld {
//    Logger logger = LoggerFactory.getLogger(CaseFileProcessingServiceOld.class);
//
//    @Autowired
//    S3Service s3Service;
//
//    @Autowired
//    CaseUploadDetailsRepo caseUploadDetailsRepository;
//
//    @Autowired
//    CaseRepository caseRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    TemplateRepo templateRepo;
//
//    @Autowired
//    VenkyNotificationService venkyNotificationService;
//    @Autowired
//    CaseProcessor caseProcessor;
//
//    //  @Async
//    public void processCaseFile(CaseUploadRequest caseUploadRequest) {
//        logger.info("Case Upload Details {}", caseUploadRequest);
//        String claimantAdminId = caseUploadRequest.getClaimantAdminId();
//        Optional<User> user = userRepository.findById(claimantAdminId);
//        User uploadedBy = userRepository.findById(caseUploadRequest.getUserId()).get();
//        if (user.isPresent()) {
//            User claimant = user.get();
//            logger.info("claimant {}", claimant);
//            logger.info("uploaded By {}", uploadedBy);
//            String yaml = templateRepo.findById(caseUploadRequest.getCsvMapperId()).get().getText();
//            CaseUploadDetails uploadDetails = CaseUploadDetails.builder().claimantAdmin(claimant).uploadedBy(uploadedBy).file(caseUploadRequest.getFileName()).remarks(caseUploadRequest.getRemarks()).yaml(yaml).title(caseUploadRequest.getRemarks()).monthYear(caseUploadRequest.getMonthYear()).createdAt(Instant.now()).build();
//            uploadDetails.setStatus("In Progress");
//            CaseUploadDetails caseUploadDetails = caseUploadDetailsRepository.save(uploadDetails);
//            processCSV(caseUploadDetails);
//            // processCSV_second(caseUploadDetails);
//        }
//
//    }
//
//    //  @SneakyThrows
//    public void processCSV(CaseUploadDetails caseUploadDetails) {
//        logger.info("Processing CaseUploadDetails {}", caseUploadDetails);
//        String yaml = caseUploadDetails.getYaml();
//        String key = "cases/" + caseUploadDetails.getUploadedBy().getId() + "/" + caseUploadDetails.getFile();
//        String csvPath = s3Service.localFilePath(key);
//        Instant now = Instant.now();
//        String missingFile = now.toEpochMilli() + "_error.csv";
//        // s3Service.createEmptyMissingFile(caseUploadDetails.getUploadedBy().getId(),missingFile);
//        List<Case> cases = null;
//        try {
//            cases = caseProcessor.processCases(yaml, csvPath, missingFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        caseUploadDetails.setSuccess(cases.size());
//        //      caseUploadDetails.setFailed(2);
//        caseUploadDetails.setErrorFile(missingFile);
//        caseUploadDetails.setStatus("Completed");
//        CaseUploadDetails uploadDetails = caseUploadDetailsRepository.save(caseUploadDetails);
//        cases.stream().forEach(aCase -> {
//            aCase.setMonthYear(caseUploadDetails.getMonthYear());
//            aCase.setStatus("NEW");
//            aCase.setClaimantAdmin(caseUploadDetails.getClaimantAdmin());
//            aCase.setCreatedBy(caseUploadDetails.getUploadedBy());
//            // aCase.setAssignedArbitrator(defaultArbitrator);
//            aCase.setCaseUploadDetails(uploadDetails.getId());
//            CaseHistory caseHistory0 = CaseHistory.builder().descriptions("CSV uploaded by: " + caseUploadDetails.getUploadedBy().getName()).date(now).build();
//            CaseHistory caseHistory1 = CaseHistory.builder().descriptions("First Arbitration Notice Send").date(now).build();
//            // CaseHistory caseHistory2=CaseHistory.builder().descriptions("Second Arbitration Notice Send").date(Instant.now().plus(120, ChronoUnit.MINUTES)).build();
//            aCase.setHistory(Arrays.asList(caseHistory0, caseHistory1));
//        });
//        List<Case> finalCases = caseRepository.saveAll(cases);
//        try {
//            venkyNotificationService.sendNotice(finalCases, "first");
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public ResponseEntity<List<CaseUploadDetails>> myUploads(String userId) {
//        return new ResponseEntity<>(caseUploadDetailsRepository.findAll(), HttpStatus.OK);
//    }
//
//}
