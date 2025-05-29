package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@Data
public class ZohoOauthDetails {

    @Id
    private String id;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String refreshToken;
    private String scope;
    private String zsoid;
    private String zuid;


}
