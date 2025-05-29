package com.kasa.adr.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseUploadRequest {

    String monthYear;
    String fileName;
    String claimantAdminId;
    String remarks;
    String csvMapperId;
    String userId;
}
