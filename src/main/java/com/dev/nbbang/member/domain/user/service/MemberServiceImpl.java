package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.api.KaKaoUserInfoResponse;
import com.dev.nbbang.member.domain.user.api.SocialOauth;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.user.util.SocialLoginIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final SocialOauth socialOauth;
    private final MemberRepository memberRepository;

    @Override
    public void kakaoLogin(SocialLoginType socialLoginType, String code) {
        // 1. 소셜 로그인해서 정보 가져오기
        KaKaoUserInfoResponse kaKaoUserInfoResponse = socialOauth.requestKakaoUserInfo(code);
        System.out.println("kaKaoUserInfoResponse = " + kaKaoUserInfoResponse.getId());
        System.out.println("kaKaoUserInfoResponse.getProperties().getNickname() = " + kaKaoUserInfoResponse.getProperties().getNickname());
        System.out.println("kaKaoUserInfoResponse.getProperties().getProfileImage() = " + kaKaoUserInfoResponse.getProperties().getProfileImage());

        // 공통 유틸로 타입보고 확인해서 아이디 생성기 만들기
        SocialLoginIdUtil socialLoginIdUtil = new SocialLoginIdUtil(socialLoginType, kaKaoUserInfoResponse.getId());

        // 3. 소셜 로그인 성공한 경우 회원 DB에 최초 가입인지 확인
        Optional<Member> findMember = memberRepository.findByMemberId(socialLoginIdUtil.getMemberId());

        // 3-1. 최초 로그인이 아닌 경우
        findMember.ifPresent(member -> {
            /**
             * @return JWTtoken
             */
        });

        // 3-2. 최초 회원 가입인 경우

        // 2. 소셜 로그인 정보 실패한 경우 에러 던지기 (현홍)
    }
}
