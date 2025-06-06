package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class ProcessSteps {

    @Id
    String id;
    String name;
    String noticeTemplateId;
    String emailTemplateId;
    int days;
}
