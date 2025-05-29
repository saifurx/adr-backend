package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NoticeRequest {
    String uploadId;
    String sequence;

}
