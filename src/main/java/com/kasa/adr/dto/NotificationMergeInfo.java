package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class NotificationMergeInfo {

    String verify_account_link;
    String toEmail;
    String name;
    Map<String, String> keyValues = new HashMap<>();


}
