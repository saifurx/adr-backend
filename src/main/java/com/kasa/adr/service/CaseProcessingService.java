package com.kasa.adr.service;


import com.kasa.adr.dto.CaseUploadRequest;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.model.CaseUploadDetails;
import com.kasa.adr.model.User;
import com.kasa.adr.repo.CaseHistoryDetailsRepo;
import com.kasa.adr.repo.CaseRepository;
import com.kasa.adr.repo.CaseUploadDetailsRepo;
import com.kasa.adr.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CaseProcessingService {

    @Autowired
    CaseUploadDetailsRepo caseUploadDetailsRepo;

    @Autowired
    CaseRepository caseRepository;

    @Autowired
    CaseHistoryDetailsRepo caseHistoryDetailsRepo;
    @Autowired
    private UserRepository userRepository;

    public void processCaseFile(CaseUploadRequest caseUploadRequest) {
        User user = userRepository.findById(caseUploadRequest.getUserId()).get();
        User climantAdmin =userRepository.findById(caseUploadRequest.getClaimantAdminId()).get();
        CaseUploadDetails caseUploadDetails=CaseUploadDetails.builder().file(caseUploadRequest.getFileName()).claimantAdmin(climantAdmin).arbitratorIds(caseUploadRequest.getArbitrators()).monthYear(caseUploadRequest.getMonthYear()).createdAt(Instant.now()).build();

        caseUploadDetailsRepo.save(caseUploadDetails);

    }



    private List<Case> getCaseList(CaseUploadRequest caseUploadRequest) {
    return new ArrayList<>();
    }

    public List<CaseUploadDetails> myUploads(String userId) {

        return caseUploadDetailsRepo.findAll();
    }
}
