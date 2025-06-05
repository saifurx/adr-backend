package com.kasa.adr.repo;


import com.kasa.adr.model.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortenerRepo extends MongoRepository<ShortUrl, String> {

    Optional<ShortUrl> findByShortCode(String shortCode);
}
