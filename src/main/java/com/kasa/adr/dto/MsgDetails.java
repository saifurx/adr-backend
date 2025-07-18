package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class MsgDetails {

    List<String> caseIds;
    Instant scheduledTime;
    String template;
    String userId;
}
