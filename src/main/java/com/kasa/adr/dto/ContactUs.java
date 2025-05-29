package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ContactUs {
    String fullName;
    String email;
    String mobile;
    String subject;
    String description;
    String publicIp;

}
