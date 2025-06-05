package com.kasa.adr.service.external;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

@Service
public class MSG91Service {

    public void sendSMS(String mobile, String name, String amount, String url) {

        try {
            Unirest.post("https://control.msg91.com/api/v5/flow")
                    .header("authkey", "443455AzKezrXwS67e1408aP1")
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .body("{\n  \"template_id\": \"67fe77bbd6fc051b294de972\",\n  \"short_url\": \"0\",\n  \"short_url_expiry\": \"680000\",\n  \"realTimeResponse\": \"1\", \n  \"recipients\": [\n    {\n      \"mobiles\": \"91" + mobile + "\",\n\"name\": \"" + name + "\",\n\"amount\": \"" + amount + "\",\n\"url\":\"" + url + "\"\n}]}")
                    .asString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWhatsAppMeeting(String name, String mobile, String url, String id, String time) {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("https://api.msg91.com/api/v5/whatsapp/whatsapp-outbound-message/bulk/")
                    .header("Content-Type", "application/json")
                    .header("authkey", "443455AzKezrXwS67e1408aP1")
                    .header("Cookie", "HELLO_APP_HASH=cnNJZEY0NFpyRXVPUUEvcnBvcC9zeVhZa1h0T0xHc3VnMWgrREU0bHBjMD0%3D; PHPSESSID=44iv8qrcm7m5ejirsg1i069710")
                    .body("{\r\n\"integrated_number\": \"919644889954\",\r\n\"content_type\":\"template\",\r\n\"payload\": {\r\n\"messaging_product\": \"whatsapp\",\r\n\"type\": \"template\",\r\n\"template\": {\r\n\"name\": \"uttara_invocation\",\r\n\"language\": {\r\n\"code\": \"en\",\r\n\"policy\": \"deterministic\"\r\n},\r\n\"namespace\": \"025f31c8_fbdf_4146_ac4c_3c90a9285fb4\",\r\n\"to_and_components\": [\r\n{\r\n\"to\": [\r\n\"" + mobile + "\"\r\n],\r\n\"components\": {\r\n\"body_1\": {\r\n\"type\": \"text\",\r\n\"value\": \"" + name + "\"\r\n},\r\n\"body_2\": {\r\n\"type\": \"text\",\r\n\"value\": \"" + url + "\"\r\n},\r\n\"body_3\": {\r\n\"type\": \"text\",\r\n\"value\": \"" + id + "\"\r\n                        },\r\n                        \"body_4\": {\r\n                            \"type\": \"text\",\r\n                            \"value\": \"" + time + "\"\r\n}\r\n}\r\n}\r\n]\r\n}\r\n}\r\n}")
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean validateOtpToken(String token) {
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("https://control.msg91.com/api/v5/widget/verifyAccessToken")
                    .header("Content-Type", "application/json")
                    .body("{\r\n  \"authkey\": \"443455AzKezrXwS67e1408aP1\",\r\n  \"access-token\": \"" + token + "\"\r\n}")
                    .asString();

            if (response.getStatus() == 200) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
