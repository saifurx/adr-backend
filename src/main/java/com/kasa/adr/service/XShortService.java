package com.kasa.adr.service;

import com.kasa.adr.model.ShortUrl;
import com.kasa.adr.repo.UrlShortenerRepo;
import com.kasa.adr.service.xshort.ShortCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class XShortService {
    @Autowired
    ShortCodeGenerator shortCodeGenerator;
    @Autowired
    UrlShortenerRepo urlShortenerRepo;
    private Logger logger = LoggerFactory.getLogger(XShortService.class);

    // @PostMapping(value = "/create-short")
//    public ShortUrl createTinyURL(RequestShortUrl request) {
//        String shortCode = shortCodeGenerator.generateShortCode(8);
//        ShortUrl tinyURL = new ShortUrl();
//        tinyURL.setShortCode(shortCode);
//        tinyURL.setLongURL(request.getLongUrl());
//        tinyURL.setCreatedAt(Instant.now());
//        ShortUrl result = null;
//        try {
//            result = urlShortenerRepo.save(tinyURL);
//        } catch (MongoException e) {
//           // createTinyURL(request);
//        }
//        return result;
//    }

    //  @GetMapping(value = "{shortUrl}")
    public ResponseEntity<Void> getAndRedirect(String code) throws NoSuchFieldException {

        ShortUrl byShortCode = urlShortenerRepo.findByShortCode(code).orElseThrow();
        if (byShortCode == null) throw new NoSuchFieldException();
        String longUrl = byShortCode.getLongURL();

        //TODO handle exception

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}
