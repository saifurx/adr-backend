package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AdminCreateRequest {

    String name;
    String email;
    String mobile;
}
