package com.dev.nbbang.member.domain.user.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)     // Snake case -> Camel Case
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAccessTokenResponse {
    private String accessToken;
    private String refreshToken;
//    private String tokenType;
//    private int expiresIn;
//    private String scope;
//    private int refreshTokenExpiresIn;
}