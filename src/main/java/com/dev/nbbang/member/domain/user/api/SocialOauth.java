package com.dev.nbbang.member.domain.user.api;

import java.util.Map;

public interface SocialOauth {
    String getOauthRedirectURL();

    String requestAccessToken(String code);

    String requestUserInfo(Map<String, String> map);

    default SocialLoginType type() {
        if(this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        } else {
            return null;
        }
    }
}
