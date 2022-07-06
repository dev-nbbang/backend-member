package com.dev.nbbang.member.domain.user.api.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialType;
import com.dev.nbbang.member.domain.user.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.member.global.exception.NbbangException;

public interface SocialOauth {
    String generateAccessToken(String refreshToken);

    Boolean unlinkSocial(String memberId, String accessToken);

    default SocialType type(String memberId) {
        if(memberId.startsWith("K-")) return SocialType.KAKAO;
        if(memberId.startsWith("G-")) return SocialType.GOOGLE;

        throw new IllegalSocialTypeException("잘못된 회원 아이디입니다.", NbbangException.ILLEGAL_SOCIAL_TYPE);
    }

    default Boolean logout(String memberId, String accessToken) {
        if(memberId.startsWith("G-")) return true;
        return false;
    }
}
