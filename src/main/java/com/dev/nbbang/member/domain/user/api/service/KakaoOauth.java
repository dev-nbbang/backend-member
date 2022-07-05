package com.dev.nbbang.member.domain.user.api.service;

import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.global.exception.NbbangException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 주입해준 value 값들 공통으로 정의할 수 있는지 확인해보기
    @Value("${sns.kakao.client-id}")
    private String clientId;

    @Value("${sns.kakao.client-secret}")
    private String clientSecret;

    @Value("${sns.kakao.regenerate-token-uri}")
    private String regenerateTokenUri;

    @Value("${sns.kakao.unlink-uri}")
    private String unlinkUri;

    @Override
    public String generateAccessToken(String refreshToken) {

        System.out.println("clientId = " + clientId);
        System.out.println("clientSecret = " + clientSecret);
        System.out.println("regenerateTokenUri = " + regenerateTokenUri);

        // Header 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 파라미터 세팅
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", refreshToken);
        params.add("client_secret", clientSecret);

        // HTTP 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // RestTempalte POST 요청
        ResponseEntity<String> response = restTemplate.postForEntity(regenerateTokenUri, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                Map<String, String> tokenInformation = objectMapper.readValue(response.getBody(), Map.class);

                return tokenInformation.get("access_token");
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
        headers.add("Authorization", "Bearer " + accessToken);

        // HTTP 객체 설정
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        // POST 요청
        ResponseEntity<String> response = restTemplate.postForEntity(unlinkUri, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK)
            throw new FailDeleteMemberException("카카오 연결 해제에 실패했습니다.", NbbangException.NOT_FOUND_MEMBER);

        return true;
    }
}
