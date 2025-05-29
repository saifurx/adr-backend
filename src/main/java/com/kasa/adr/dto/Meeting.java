package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Meeting {

    private String topic;
    private String agenda;
    private long presenter;
    private String startTime;
    private long duration;
    private String timezone;
    private List<String> participants;
}