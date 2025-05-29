package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateRequest {

    String claimant;
    String name;
    String status;
    String subject;
    String text;
    String type;

}
