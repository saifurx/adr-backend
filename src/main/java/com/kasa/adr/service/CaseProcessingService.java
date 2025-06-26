package com.kasa.adr.service;


import com.kasa.adr.dto.CaseUploadRequest;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.model.CaseUploadDetails;
import com.kasa.adr.repo.CaseHistoryDetailsRepo;
import com.kasa.adr.repo.CaseRepository;
import com.kasa.adr.repo.CaseUploadDetailsRepo;
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
    CaseHistoryDetailsRepo caseHistoryDetailsRepo;

    public void processCaseFile(CaseUploadRequest caseUploadRequest) {

        List<Case> cases = getCaseList(caseUploadRequest);
        List<CaseHistoryDetails> caseHistoryDetails= new ArrayList<>();

    }



    private List<Case> getCaseList(CaseUploadRequest caseUploadRequest) {
    return new ArrayList<>();
    }

    public List<CaseUploadDetails> myUploads(String userId) {
        return new ArrayList<>();
    }
}
