package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Data
@Document
public class PasswordResetToken {

    @Id
    private String id;
    private String token;
    private String userName;
    private Instant expiryDate;

}