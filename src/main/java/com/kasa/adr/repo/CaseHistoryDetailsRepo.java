package com.kasa.adr.repo;

import com.kasa.adr.model.CaseHistoryDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseHistoryDetailsRepo extends MongoRepository<CaseHistoryDetails, String> {

    List<CaseHistoryDetails> findByCaseId(String caseId);
}
