package com.kasa.adr.service;


import com.kasa.adr.dto.CaseUploadRequest;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.model.CaseUploadDetails;
import com.kasa.adr.model.JobDetails;
import com.kasa.adr.repo.CaseHistoryDetailsRepo;
import com.kasa.adr.repo.CaseRepository;
import com.kasa.adr.repo.CaseUploadDetailsRepo;
import com.kasa.adr.repo.JobDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CaseProcessingService {

    @Autowired
    CaseUploadDetailsRepo caseUploadDetailsRepo;

    @Autowired
    CaseRepository caseRepository;

    @Autowired
    JobDetailsRepo jobDetailsRepo;

    @Autowired
    CaseHistoryDetailsRepo caseHistoryDetailsRepo;

    public void processCaseFile(CaseUploadRequest caseUploadRequest) {

        List<Case> cases = getCaseList(caseUploadRequest);
        List<JobDetails> jobDetailsList = createJobDetails(caseUploadRequest);
        List<CaseHistoryDetails> caseHistoryDetails= new ArrayList<>();

    }

    private List<JobDetails> createJobDetails(CaseUploadRequest caseUploadRequest) {

        List<JobDetails> jobDetailsList = new ArrayList<>();

    // Logic to create job details based on the case upload request
    // This is a placeholder, actual implementation will depend on the business logic
        return jobDetailsList;
        }

    private List<Case> getCaseList(CaseUploadRequest caseUploadRequest) {
    return new ArrayList<>();
    }

    public List<CaseUploadDetails> myUploads(String userId) {
        return new ArrayList<>();
    }
}
