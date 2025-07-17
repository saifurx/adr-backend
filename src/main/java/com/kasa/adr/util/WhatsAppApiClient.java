package com.kasa.adr.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WhatsAppApiClient {
    
    private static final String API_URL = "https://api.msg91.com/api/v5/whatsapp/whatsapp-outbound-message/bulk/";
    private static final String INTEGRATED_NUMBER = "919644889954";
    private static final String NAMESPACE = "025f31c8_fbdf_4146_ac4c_3c90a9285fb4";
    
    private final String authKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public WhatsAppApiClient(String authKey) {
        this.authKey = authKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    // Main method to send WhatsApp bulk message
    public String sendBulkMessage(String templateName, List<ToAndComponent> toAndComponents) throws IOException, InterruptedException {
        // Create the request payload
        WhatsAppRequest request = new WhatsAppRequest();
        request.integratedNumber = INTEGRATED_NUMBER;
        request.contentType = "template";
        
        // Create payload
        Payload payload = new Payload();
        payload.messagingProduct = "whatsapp";
        payload.type = "template";
        
        // Create template
        Template template = new Template();
        template.name = templateName;
        template.language = new Language("en", "deterministic");
        template.namespace = NAMESPACE;
        template.toAndComponents = toAndComponents;
        
        payload.template = template;
        request.payload = payload;
        
        // Convert to JSON
        String jsonBody = objectMapper.writeValueAsString(request);
        
        // Create HTTP request
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("authkey", authKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        // Send request
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
    
    // Helper method to create ToAndComponent
    public static ToAndComponent createToAndComponent(List<String> phoneNumbers, Map<String, String> bodyValues) {
        ToAndComponent toAndComponent = new ToAndComponent();
        toAndComponent.to = phoneNumbers;
        
        Components components = new Components();
        
        // Set body values (body_1 to body_7)
        for (int i = 1; i <= 7; i++) {
            String key = "body_" + i;
            String value = bodyValues.getOrDefault(key, "default_value");
            
            switch (i) {
                case 1: components.body1 = new BodyComponent("text", value); break;
                case 2: components.body2 = new BodyComponent("text", value); break;
                case 3: components.body3 = new BodyComponent("text", value); break;
                case 4: components.body4 = new BodyComponent("text", value); break;
                case 5: components.body5 = new BodyComponent("text", value); break;
                case 6: components.body6 = new BodyComponent("text", value); break;
                case 7: components.body7 = new BodyComponent("text", value); break;
            }
        }
        
        toAndComponent.components = components;
        return toAndComponent;
    }
    
    // Example usage
    public static void main(String[] args) {
        try {
            WhatsAppApiClient client = new WhatsAppApiClient("443455AzKezrXwS67e1408aP1");
            
            // Create phone numbers list
            List<String> phoneNumbers = List.of("919854087006");
            
            // Create body values
            Map<String, String> bodyValues = Map.of(
                "body_1", "Hello John",
                "body_2", "Meeting scheduled",
                "body_3", "Tomorrow at 3 PM",
                "body_4", "Conference Room A",
                "body_5", "Please confirm",
                "body_6", "Best regards",
                "body_7", "Team"
            );
            
            // Create to and components
            ToAndComponent toAndComponent = createToAndComponent(phoneNumbers, bodyValues);
            List<ToAndComponent> toAndComponents = List.of(toAndComponent);
            
            // Send message
            String response = client.sendBulkMessage("uttara_invocation", toAndComponents);
            System.out.println("Response: " + response);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Data classes for JSON serialization
    public static class WhatsAppRequest {
        @JsonProperty("integrated_number")
        public String integratedNumber;
        
        @JsonProperty("content_type")
        public String contentType;
        
        public Payload payload;
    }
    
    public static class Payload {
        @JsonProperty("messaging_product")
        public String messagingProduct;
        
        public String type;
        public Template template;
    }
    
    public static class Template {
        public String name;
        public Language language;
        public String namespace;
        
        @JsonProperty("to_and_components")
        public List<ToAndComponent> toAndComponents;
    }
    
    public static class Language {
        public String code;
        public String policy;
        
        public Language(String code, String policy) {
            this.code = code;
            this.policy = policy;
        }
    }
    
    public static class ToAndComponent {
        public List<String> to;
        public Components components;
    }
    
    public static class Components {
        @JsonProperty("body_1")
        public BodyComponent body1;
        
        @JsonProperty("body_2")
        public BodyComponent body2;
        
        @JsonProperty("body_3")
        public BodyComponent body3;
        
        @JsonProperty("body_4")
        public BodyComponent body4;
        
        @JsonProperty("body_5")
        public BodyComponent body5;
        
        @JsonProperty("body_6")
        public BodyComponent body6;
        
        @JsonProperty("body_7")
        public BodyComponent body7;
    }
    
    public static class BodyComponent {
        public String type;
        public String value;
        
        public BodyComponent(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}

// Maven dependencies needed in pom.xml:
/*
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
</dependencies>
*/