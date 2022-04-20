package com.dev.nbbang.member.domain.account.api.service;

import com.dev.nbbang.member.domain.account.api.dto.request.ImpToken;
import com.dev.nbbang.member.domain.account.dto.request.CardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImportAPI {
    private final RestTemplate restTemplate;

    @Value("${imp.token.url}")
    private String impTokenUrl;
    //customer_uid필요
    @Value("${imp.billing.url}")
    private String impBillingUrl;
    @Value("${imp.client.key}")
    private String impKey;
    @Value("${imp.client.secret}")
    private String impSecret;

    public String getAccessToken() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imp_key", impKey);
        jsonObject.put("imp_secret", impSecret);

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(impTokenUrl, impRequest, String.class);
            if (impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                ImpToken getBody = mapper.readValue(impResponse.getBody(), ImpToken.class);
                log.info(getBody.getResponse().getAccessToken());
                return getBody.getResponse().getAccessToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new Exception("server 접근 실패");
    }

    public String getBillingKey(String accessToken, CardRequest card, String memberId) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", accessToken);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("card_number", card.getCardNumber());
        jsonObject.put("expiry", card.getExpiry());
        jsonObject.put("birth", card.getBirth());
        jsonObject.put("pwd_2digit", card.getPwd2digit());
        String cutomerUid = memberId + randomString();
        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);
        String url = impBillingUrl + "/"+cutomerUid;
        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(url, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
                log.info(map.toString());
                if(!map.get("code").toString().equals("0")) throw new Exception("billingKey 발급 실패");
                return cutomerUid;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new Exception("billingKey 발급 실패");
    }

    public void deleteBillingKey(String accessToken, String customerUid) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", accessToken);

        HttpEntity impRequest = new HttpEntity<>(httpHeaders);
        String uri = impBillingUrl + "/" + customerUid + "?reason=deleteKey";

        try {
            ResponseEntity<String> impResponse = restTemplate.exchange(uri, HttpMethod.DELETE, impRequest, String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
            if(!map.get("code").toString().equals("0")) throw new Exception("errorCode: " + map.get("code").toString() +"billingKey 삭제 실패");
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new Exception("billingKey 삭제 실패");
    }

    public String randomString() {
        Random random = new Random();
        return random.ints(48, 123).filter(i -> (i<=57 || i>= 65) && (i<=90 || i>=97))
                .limit(6).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder:: append).toString();
    }

}
