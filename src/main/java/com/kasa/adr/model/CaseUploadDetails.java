package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@Document
public class CaseUploadDetails {

    @Id
    String id;

    String file;
    Instant createdAt;
    String monthYear;
    @DBRef
    User uploadedBy;
    @DBRef
    User claimantAdmin;
    @DBRef
    List<User> arbitrators;
    String templateId;
    int total;


}
