package com.kasa.adr.service.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

@Service
public class MeetingService {


    @Autowired
    ZohoMeeting zohoMeeting;

    //@PostConstruct
    public HttpResponse<String> scheduleMeeting(String scheduledTime, String email, String zuid) {
        HttpResponse<String> response = null;
        try {
            String meetingJson = zohoMeeting.getJson(scheduledTime, zuid, email);
            response = zohoMeeting.scheduleMeeting(meetingJson);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
