package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateStatus {
    List<String> caseIds;
    String descriptions;
    String status;
    String amountRecovered;
    String userId;
    String file;
}
