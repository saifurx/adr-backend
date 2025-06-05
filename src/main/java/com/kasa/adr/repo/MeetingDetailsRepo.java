package com.kasa.adr.repo;

import com.kasa.adr.model.MeetingDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingDetailsRepo extends MongoRepository<MeetingDetails, String> {
}
