package com.kasa.adr.dto;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Documents {
    String description;
    String fileName;
    Instant createdAt;
}
