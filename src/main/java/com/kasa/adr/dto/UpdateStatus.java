package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStatus {
    String caseId;
    String descriptions;
    String userId;
    String file;
}
