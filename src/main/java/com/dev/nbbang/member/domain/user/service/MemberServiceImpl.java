package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.response.MemberResponse;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.user.repository.OTTViewRepository;
import com.dev.nbbang.member.domain.user.api.util.SocialLoginIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final OTTViewRepository ottViewRepository;
    private final SocialTypeMatcher socialTypeMatcher;

    // 소셜타입마다 각각 다른 소셜 로그인
    public String socialLogin(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = socialTypeMatcher.findSocialOauthByType(socialLoginType);
        try {
            String socialLoginId = socialOauth.requestUserInfo(code);
            SocialLoginIdUtil socialLoginIdUtil = new SocialLoginIdUtil(socialLoginType, socialLoginId);
            return socialLoginIdUtil.getMemberId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "소셜 로그인 실패";
    }

    public MemberResponse findMember(String memberId) {
        // null 값으로 받는게 마음에 안드는데 해결책 필요
        Member member = memberRepository.findByMemberId(memberId).orElse(null);
        return MemberResponse.create(member);
    }

    public Member memberSave(Member member) {
        return memberRepository.save(member);
    }

    public OTTView findByOttId(int ottId) {
        return ottViewRepository.findByOttId(ottId);
    }

}
