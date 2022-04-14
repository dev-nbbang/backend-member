package com.dev.nbbang.member.domain.user.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class KakaoAuthUrl implements SocialAuthUrl {
    @Value("${sns.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${sns.kakao.client-id}")
    private String clientId;

    @Override
    public String makeAuthorizationUrl() {
        System.out.println("clientId = " + clientId);
        System.out.println("redirectUri = " + redirectUri);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("kauth.kakao.com")
                .path("/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build(true);

        System.out.println("builder.toUriString() = " + uriComponents.toUriString());

        return uriComponents.toUriString();
    }
}
