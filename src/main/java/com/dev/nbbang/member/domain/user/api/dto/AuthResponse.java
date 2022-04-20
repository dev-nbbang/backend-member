package com.dev.nbbang.member.domain.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthResponse {
    private String authUrl;

    public static AuthResponse create(String url) {
        return AuthResponse.builder().authUrl(url).build();
    }
}
