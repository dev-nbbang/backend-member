package com.dev.nbbang.member.domain.user.api.service;
import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;

public interface SocialOauth {
    String requestAccessToken(String code);

    String requestUserInfo(String code);

    default SocialLoginType type() {
        if(this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        }
        else if(this instanceof KakaoOauth){
            return SocialLoginType.KAKAO;
        }
        return null;
    }
}
