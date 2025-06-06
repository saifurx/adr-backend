package com.kasa.adr.service;

import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.repo.CaseHistoryDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseHistoryDetailsService {

    @Autowired
    private CaseHistoryDetailsRepo caseHistoryDetailsRepo;

     public CaseHistoryDetails createCaseHistoryDetail(CaseHistoryDetails details) {
         return caseHistoryDetailsRepo.save(details);
     }
     public List<CaseHistoryDetails> findByCaseId(String caseId) {
         return caseHistoryDetailsRepo.findByCaseId(caseId);
     }

}
