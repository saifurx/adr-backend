package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateRequest {

    String claimantAdminId;
    String name;
    boolean status;
    String emailSubject;
    String emailBody;
    String smsTemplateId;
    String whatsAppTemplateId;
    String attachmentText;

}
