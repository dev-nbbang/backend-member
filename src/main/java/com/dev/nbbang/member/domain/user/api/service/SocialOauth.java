package com.dev.nbbang.member.domain.user.api.service;
import com.dev.nbbang.member.domain.user.api.dto.KaKaoUserInfoResponse;
import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;

import java.util.Map;

public interface SocialOauth {
    String getOauthRedirectURL();

    String requestAccessToken(String code);

    String requestUserInfo(String code);

    default SocialLoginType type() {
        if(this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        } else {
            return null;
        }
    }
}
