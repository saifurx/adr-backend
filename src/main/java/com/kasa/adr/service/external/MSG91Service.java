package com.kasa.adr.service.external;


import com.kasa.adr.model.CaseDetails;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MSG91Service {
    Logger logger = LoggerFactory.getLogger(MSG91Service.class);


    public boolean validateOtpToken(String token) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("https://control.msg91.com/api/v5/widget/verifyAccessToken")
                    .header("Content-Type", "application/json")
                    .body("{\r\n  \"authkey\": \"443455AzKezrXwS67e1408aP1\",\r\n  \"access-token\": \"" + token + "\"\r\n}")
                    .asString();
            logger.info("Response from MSG91: " + response.getBody());
            if (response.getStatus() == 200) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendFirstSMSByTemplateId(CaseDetails aCase, String url, String smsTemplateId) {
        try {
            Unirest.post("https://control.msg91.com/api/v5/flow")
                    .header("authkey", "443455AzKezrXwS67e1408aP1")
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .body("{\n  \"template_id\": \""+smsTemplateId+"\",\n  \"short_url\": \"0\",\n  \"short_url_expiry\": \"680000\",\n  \"realTimeResponse\": \"1\", \n  \"recipients\": [\n    {\n      \"mobiles\": \"91" + aCase.getCustomerContactNumber() + "\",\n\"var1\": \"" + aCase.getClaimantAdmin().getName() + "\"}]}")
                    .asString();
            logger.info("SMS sent successfully to " + aCase.getCustomerContactNumber() + " with template ID: " + smsTemplateId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFirstWhatsAppMsgTemplateId(CaseDetails aCase, String url, String whatsAppTemplateId) {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("https://api.msg91.com/api/v5/whatsapp/whatsapp-outbound-message/bulk/")
                    .header("Content-Type", "application/json")
                    .header("authkey", "443455AzKezrXwS67e1408aP1")
                    .header("Cookie", "HELLO_APP_HASH=cnNJZEY0NFpyRXVPUUEvcnBvcC9zeVhZa1h0T0xHc3VnMWgrREU0bHBjMD0%3D; PHPSESSID=44iv8qrcm7m5ejirsg1i069710")
                    .body("{\r\n\"integrated_number\": \"919644889954\",\r\n\"content_type\":\"template\",\r\n\"payload\": {\r\n\"messaging_product\": \"whatsapp\",\r\n\"type\": \"template\",\r\n\"template\": {\r\n\"name\": \""+whatsAppTemplateId+"\",\r\n\"language\": {\r\n\"code\": \"en\",\r\n\"policy\": \"deterministic\"\r\n},\r\n\"namespace\": \"025f31c8_fbdf_4146_ac4c_3c90a9285fb4\",\r\n\"to_and_components\": [\r\n{\r\n\"to\": [\r\n\"91" + aCase.getCustomerContactNumber() + "\"\r\n],\r\n\"components\": {\r\n\"body_1\": {\r\n\"type\": \"text\",\r\n\"value\": \"" + aCase.getCustomerName() + "\"\r\n},\r\n\"body_2\": {\r\n\"type\": \"text\",\r\n\"value\": \"" + aCase.getClaimantAdmin().getName() + "\"\r\n}}")
                    .asString();
            logger.info("WhatsApp message sent successfully to " + aCase.getCustomerContactNumber() + " with template ID: " + whatsAppTemplateId);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
