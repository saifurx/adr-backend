package com.kasa.adr.util;

import com.kasa.adr.dto.ZoomAuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

//@Component
public class ZoomAuthenticationHelper {

    //  @Autowired
    RestTemplate restTemplate;
    private long tokenExpiryTime;
    @Value("${zoom.clientId}")
    private String zoomClientId;
    @Value("${zoom.client.secret}")
    private String zoomClientSecret;
    @Value("${zoom.oauth2.issuer}")
    private String zoomIssuerUrl;
    @Value("${zoom.oauth2.account-id}")
    private String zoomAccountId;
    private ZoomAuthResponse authResponse;

    public synchronized String getAuthenticationToken() throws Exception {
        if (this.authResponse == null || checkIfTokenWillExpire()) {
            fetchToken();
        }
        return this.authResponse.getAuthToken();
    }

    //determine new token should be retrieved
    private boolean checkIfTokenWillExpire() {

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        long differenceInMillis = this.tokenExpiryTime - now.getTimeInMillis();

        // Token is already expired
        if (differenceInMillis < 0) {
            return true;
        }
        //Token has less than 20 minutes to expire
        if (TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) < 20) {
            return true;
        }

        return false;
    }

    private void fetchToken() throws Exception {

        ResponseEntity<ZoomAuthResponse> response = null;

        String credentials = zoomClientId + ":" + zoomClientSecret;
        String encodedCredentials = new String(Base64.getEncoder().encodeToString(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));
        headers.add("Authorization", "Basic " + encodedCredentials);
        headers.add("Host", "zoom.us");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "account_credentials");
        map.add("account_id", zoomAccountId);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        String url = zoomIssuerUrl + "/token";
        try {
            this.authResponse = restTemplate.exchange(url, HttpMethod.POST, entity,
                    ZoomAuthResponse.class).getBody();
        } catch (HttpClientErrorException e) {
            ResponseEntity<String> res = new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
            throw new Exception(
                    (String.format("Unable to get authentication token due to %s. Response code: %d", res.getBody(),
                            res.getStatusCodeValue())));
        }

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        this.tokenExpiryTime = now.getTimeInMillis() + (this.authResponse.getExpiresIn() - 10) * 1000;
    }

}