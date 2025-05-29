package com.kasa.adr.repo;

import com.kasa.adr.model.CaseUploadDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseUploadDetailsRepo extends MongoRepository<CaseUploadDetails, String> {
}
