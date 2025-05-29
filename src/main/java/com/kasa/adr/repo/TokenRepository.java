package com.kasa.adr.repo;

import com.kasa.adr.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {


    // List<Token> findAllValidTokenByUserId(String id);

    Optional<Token> findByToken(String token);

    // List<Token> findAllValidTokenByUserAndTokenType(String id, String tokenType);
}