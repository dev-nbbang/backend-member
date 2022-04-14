package com.dev.nbbang.member.domain.user.api.util;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;

public interface SocialAuthUrl {
    String makeAuthorizationUrl();

    default SocialLoginType type() {
        if(this instanceof GoogleAuthUrl) {
            return SocialLoginType.GOOGLE;
        } else if(this instanceof  KakaoAuthUrl) {
            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }
}