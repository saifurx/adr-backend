package com.kasa.adr.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class CaseHistoryDetails {
    @Id
    String id;
    String caseId;
    Instant date;
    String description;
    String createdBy;
    String status;
    String documentUrl;
    String recordingUrl;
    String sourceIp;

}
