package com.kasa.adr.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestShortUrl {
    String longUrl;
    String expiryAt;
}
