package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class Template {
    @Id
    String id;
    String name;
    String emailSubject;
    String emailBody;
    String smsTemplateId;
    String whatsAppTemplateId;
    String attachmentText;
    Instant createdAt;
    boolean status;
    @DBRef
    User claimantAdminUser;
}
