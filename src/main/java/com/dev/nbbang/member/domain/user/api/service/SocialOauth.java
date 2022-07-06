package com.dev.nbbang.member.domain.user.api.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialType;
import com.dev.nbbang.member.domain.user.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.member.global.exception.NbbangException;

public interface SocialOauth {
    String generateAccessToken(String refreshToken);

    Boolean unlinkSocial(String memberId, String accessToken);

    default SocialType type() {
        if(this instanceof GoogleOauth) return SocialType.GOOGLE;
        if(this instanceof KakaoOauth) return SocialType.KAKAO;
        return null;
    }

    default Boolean logout(String memberId, String accessToken) {
        if(memberId.startsWith("G-")) return true;
        return false;
    }
}
