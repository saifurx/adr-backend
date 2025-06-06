package com.kasa.adr.repo;

import com.kasa.adr.model.JobDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDetailsRepo extends MongoRepository<JobDetails, String> {
}
