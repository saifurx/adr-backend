package com.kasa.adr.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Data
@Document
public class MeetingDetails {
    @Id
    private String id;
    private String caseId;
    private String meetingId;
    private LocalDateTime scheduledTime;
    private String recodingUrl;


}
