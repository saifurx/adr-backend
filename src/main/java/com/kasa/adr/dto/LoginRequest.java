package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    String email;
    String mobile;
    boolean mobileLogin;

    String publicIp;
    String password;
    boolean otpLogin;

}
