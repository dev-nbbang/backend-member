package com.dev.nbbang.member.domain.user.api;

public interface SocialOauth {
    String getOauthRedirectURL();

    /**
     *
     * @param code (Authorization Code)
     * @return accessToken
     */
    String requestAccessToken(String code);

    KaKaoUserInfoResponse requestKakaoUserInfo(String code);
}
