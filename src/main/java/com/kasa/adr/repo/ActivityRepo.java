package com.kasa.adr.repo;


import com.kasa.adr.model.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepo extends MongoRepository<ActivityLog, String> {

    @Query(value = "{ 'userid' : ?0}", sort = "{'activityAt': -1}")
    List<ActivityLog> findByUserid(String userid);
}
