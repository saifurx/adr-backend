package com.kasa.adr.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document
public class CaseProcessConfig {
    @Id
    String id;
    String claimantAdminId;
    String name;
    String description;
    List<ProcessSteps> processSteps;
}
