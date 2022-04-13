package com.dev.nbbang.member.domain.user.api.util;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SocialTypeMatcher {

    private final List<SocialOauth> socialOauthList;
    private final List<SocialAuthUrl> socialAuthUrlList;

    // 소셜 로그인 타입 확인
    public SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(socialOauth -> socialOauth.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLogin Type 입니다"));
    }


    public SocialAuthUrl findSocialAuthUrlByType(SocialLoginType socialLoginType) {
        return socialAuthUrlList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLogin Tpye 입니다"));
    }
}
