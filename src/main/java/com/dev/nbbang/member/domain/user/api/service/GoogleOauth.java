package com.dev.nbbang.member.domain.user.api.service;

import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.global.exception.NbbangException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@RefreshScope
public class GoogleOauth implements SocialOauth {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 정보 주입받기
    @Value("${sns.google.client.id}")
    private String CLIENT_ID;

    @Value("${sns.google.client.secret}")
    private String CLIENT_SECRET;

    @Value("${sns.google.token.uri}")
    private String TOKEN_URI;

    @Value("${sns.google.revoke.uri}")
    private String REVOKE_URI;

    @Override
    public String generateAccessToken(String refreshToken) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        // 요청 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URI, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                Map tokenInformation = objectMapper.readValue(response.getBody(), Map.class);

                return tokenInformation.get("access_token").toString();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        throw new FailDeleteMemberException("엑세스 토큰 재발급에 실패했습니다.", NbbangException.NOT_FOUND_MEMBER);
    }

    @Override
    public Boolean unlinkSocial(String memberId, String accessToken) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", accessToken);

        // 요청 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(REVOKE_URI, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK)
            throw new FailDeleteMemberException("구글 연결 해제에 실패했습니다.", NbbangException.NOT_FOUND_MEMBER);

        return true;
    }
}
