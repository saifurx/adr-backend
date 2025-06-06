package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class JobDetails {

    @Id
    String id;
    String jobName;
    String caseUploadDetailsId;
    Instant scheduledDate;
    ProcessSteps processSteps;
}
