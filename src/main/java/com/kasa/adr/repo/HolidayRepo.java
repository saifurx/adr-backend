package com.kasa.adr.repo;


import com.kasa.adr.model.Holiday;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayRepo extends MongoRepository<Holiday, String> {

    @Query(value = "{ 'year' : ?0}")
    List<Holiday> findAllByYear(String year);
}
