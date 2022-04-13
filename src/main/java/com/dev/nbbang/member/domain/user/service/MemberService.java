package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.entity.SocialLoginType;

public interface MemberService {
    // kakao 소셜 로그인
    void kakaoLogin(SocialLoginType socialLoginType, String code);
}
