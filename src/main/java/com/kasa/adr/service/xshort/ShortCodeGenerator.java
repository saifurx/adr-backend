package com.kasa.adr.service.xshort;

import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ShortCodeGenerator {
    @Value("${short.io.apisecret}")
    private String shortIoSecret;


    public String generateShortIoUrl(String originalUrl) {
        OkHttpClient client = new OkHttpClient();
        String content = "{\\\"skipQS\\\":false,\\\"archived\\\":false,\\\"allowDuplicates\\\":false,\\\"originalURL\\\":\\\"+originalUrl+\"}";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("https://api.short.io/links")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", shortIoSecret)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Response {}" + response);
        return "";

    }

}