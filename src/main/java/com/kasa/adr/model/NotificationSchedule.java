package com.kasa.adr.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Builder
@Data
@Document
public class NotificationSchedule {

    @Id
    private String id;
    private String caseId;
    private String caseUploadId;
    private LocalDate scheduledTime;
    private String emailTemplate;
    private String noticeTemplate;
    private String smsTemplateId;
    private String whatsAppTemplateId;

}
