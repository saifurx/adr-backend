package com.kasa.adr.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeetingDetails {
    String meetingKey;
    String joinLink;
    String startTime;

}
