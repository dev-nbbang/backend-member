package com.dev.nbbang.member.domain.user.api.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialType;
import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.domain.user.exception.FailLogoutMemberException;
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
    private String CLIENT_ID;

    @Value("${sns.kakao.client-secret}")
    private String CLIENT_SECRET;

    @Value("${sns.kakao.regenerate-token-uri}")
    private String REGENERATE_TOKEN_URI;

    @Value("${sns.kakao.unlink-uri}")
    private final String UNLINK_URI;

    @Value("${sns.kakao.logout-uri}")
    private final String LOGOUT_URI;
    
    @Override
    public String generateAccessToken(String refreshToken) {
        
        System.out.println("CLIENT_ID = " + CLIENT_ID);
        System.out.println("CLIENT_SECRET = " + CLIENT_SECRET);
        System.out.println("REGENERATE_TOKEN_URI = " + REGENERATE_TOKEN_URI);

        // Header 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 파라미터 세팅
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", CLIENT_ID);
        params.add("refresh_token", refreshToken);
        params.add("client_secret", CLIENT_SECRET);

        // HTTP 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // RestTempalte POST 요청
        ResponseEntity<String> response = restTemplate.postForEntity(REGENERATE_TOKEN_URI, request, String.class);
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
        ResponseEntity<String> response = restTemplate.postForEntity(UNLINK_URI, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK)
            throw new FailDeleteMemberException("카카오 연결 해제에 실패했습니다.", NbbangException.NOT_FOUND_MEMBER);

        return true;
    }

    @Override
    public Boolean logout(String memberId, String accessToken) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        // 요청 생성
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(LOGOUT_URI, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new FailLogoutMemberException("카카오 소셜 로그아웃에 실패했습니다.", NbbangException.FAIL_TO_LOGOUT);

        return true;
    }
}
