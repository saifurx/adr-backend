package com.kasa.adr.service.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ZohoMeeting {

    private final String CLIENT_ID = "1000.8B02I33ENTR6DAAOAIP6GD1V095WPC";
    private final String CLIENT_SECRET = "423884ebac1bd878a8da9b3a938873dd7a241a1561";
    private final String REFRESH_TOKEN = "1000.4d054d7440b966aee95625767d59bee1.050d0a871fd1956d3195a755bb9c470c";
    private final String REFRESH_TOKEN_ORG = "1000.2b6642b0dcb93e4c901859bea100024b.66f35eef13d09f1423f1967e259d252a";
    private final String TOKEN_URL = "https://accounts.zoho.in/oauth/v2/token";
    private final String SCHEDULE_MEETING_URL = "https://meeting.zoho.in/api/v2/60038170864/sessions.json";

    private final String REFRESH_TOKEN_USER = "1000.c5e1bfdd2e302afea822ffb7d10c9ca7.b9048cb5181b4bd858a00e96327b6a69";

    public String getAccessToken(String scope) throws Exception {
        String urlParameters = null;
        if (scope.equalsIgnoreCase("meeting")) {
            urlParameters = "client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET +
                    "&refresh_token=" + REFRESH_TOKEN +
                    "&grant_type=refresh_token";
        } else if (scope.equalsIgnoreCase("user")) {
            urlParameters = "client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET +
                    "&refresh_token=" + REFRESH_TOKEN_USER +
                    "&grant_type=refresh_token";
        } else {
            urlParameters = "client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET +
                    "&refresh_token=" + REFRESH_TOKEN_ORG +
                    "&grant_type=refresh_token";
        }

        URL url = new URL(TOKEN_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response to get the access token
            String jsonResponse = response.toString();
            String accessToken = jsonResponse.split("\"access_token\":\"")[1].split("\"")[0];
            return accessToken;
        } else {
            throw new Exception("Failed to get access token. Response code: " + responseCode);
        }
    }

    public HttpResponse<String> scheduleMeeting(String jsonBody) throws Exception {
        String accessToken = getAccessToken("meeting");
        HttpClient client = HttpClient.newHttpClient();
        String token = "Zoho-oauthtoken " + accessToken;
        // Create an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://meeting.zoho.in/api/v2/60038170864/sessions.json"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Print the status code and response body
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
        return response;

    }


    public String getJson(String time, String zuid, String participantEmail) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // Create the participants array
        ArrayNode participants = mapper.createArrayNode();
        ObjectNode participant = mapper.createObjectNode();
        participant.put("email", participantEmail);
        participants.add(participant);

        // Create the session object
        ObjectNode session = mapper.createObjectNode();
        session.put("topic", "Virturesolve360 Arbitration Hearing");
        session.put("agenda", "Virturesolve360 Arbitration Hearing");
        session.put("presenter", zuid);
        session.put("startTime", time);
        session.put("duration", 900000);
        session.put("timezone", "Asia/Calcutta");
        session.set("participants", participants);

        // Create the root object
        ObjectNode root = mapper.createObjectNode();
        root.set("session", session);

        // Print the JSON
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }
}