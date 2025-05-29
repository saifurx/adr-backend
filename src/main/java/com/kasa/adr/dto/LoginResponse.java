package com.kasa.adr.dto;

import com.kasa.adr.model.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private User user;
}
