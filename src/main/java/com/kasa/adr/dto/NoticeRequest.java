package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class NoticeRequest {
    List<String> caseIds;
    String templateId;
    String userId;
}
