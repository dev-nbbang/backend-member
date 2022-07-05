package com.dev.nbbang.member.domain.user.api.util;

import com.dev.nbbang.member.domain.user.api.entity.SocialType;
import com.dev.nbbang.member.domain.user.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialTypeMatcher {
    private final List<SocialOauth> socialOauthList;

    public SocialOauth findSocialOauth(String memberId, SocialType socialType) {
        return socialOauthList.stream()
                .filter(socialOauth -> socialOauth.type(memberId) == socialType)
                .findFirst()
                .orElseThrow(() -> new IllegalSocialTypeException("잘못된 소셜 로그인 타입입니다.", NbbangException.ILLEGAL_SOCIAL_TYPE));
    }
}
