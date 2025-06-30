package com.kasa.adr.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseUploadRequest {

    String monthYear;
    String templateId;
    String claimantAdminId;
    String[] arbitrators;
    String userId;
    String fileName;

}
