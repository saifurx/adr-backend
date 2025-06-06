package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Data
@Document
public class CaseUploadDetails {

    @Id
    String id;
    String file;
    Instant createdAt;
    String monthYear;
    User uploadedBy;
    User claimantAdmin;
    int total;
    CaseProcessConfig caseProcessConfig;

}
