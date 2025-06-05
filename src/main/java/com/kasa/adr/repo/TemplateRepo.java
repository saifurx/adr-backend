package com.kasa.adr.repo;


import com.kasa.adr.model.Template;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepo extends MongoRepository<Template, String> {

    @Query(value = "{ 'claimantAdminUser.id' : ?0}")
    List<Template> findByClaimantAdmin(String claimantAdminId);

    @Query("{'claimantAdminUser.id' : ?0, 'name' : ?1, 'type' : ?2}")
    List<Template> findByClaimantNameAndType(ObjectId claimant, String name, String type);

    @Query("{'type' : ?0}")
    List<Template> findAllByType(String type);

    @Query("{ 'name' : ?0, 'type' : ?1}")
    List<Template> findByNameAndType(String name, String type);

}
