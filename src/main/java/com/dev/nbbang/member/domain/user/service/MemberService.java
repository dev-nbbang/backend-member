package com.dev.nbbang.member.domain.user.service;


import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;

import java.util.Map;

public interface MemberService {
    void kakaoLogin(SocialLoginType socialLoginType, String code);

    void request(SocialLoginType socialLoginType);

    Map<String, String> socialLogin(SocialLoginType socialLoginType, String code);

    SocialOauth findSocialOauthByType(SocialLoginType socialLoginType);

    Member findByMember_Id(String memberId);

    Member memberSave(Member member);

    OTTView findByOttId(int ottId);

}
