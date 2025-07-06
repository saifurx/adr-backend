package com.kasa.adr.repo;


import com.kasa.adr.model.CaseDocuments;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseDocumentsRepo extends MongoRepository<CaseDocuments, String> {
    @Query("{ 'caseId': ?0 }" )
    List<CaseDocuments> findByCaseId(String caseId);
}
