package com.kasa.adr.repo;


import com.kasa.adr.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordResetTokenRepo extends MongoRepository<PasswordResetToken, String> {
    @Query("{'token' : ?0 }")
    List<PasswordResetToken> findByToken(String token);
}
