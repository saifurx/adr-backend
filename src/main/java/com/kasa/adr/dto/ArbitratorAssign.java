package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArbitratorAssign {
    List<String> caseIds;
    String assignedArbitratorId;
    String userId;
}
