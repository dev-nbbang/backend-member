package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.user.repository.OTTViewRepository;
import com.dev.nbbang.member.domain.user.api.util.SocialLoginIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final SocialOauth socialOauth;
    private final MemberRepository memberRepository;
    private final List<SocialOauth> socialOauthList;
    private final HttpServletResponse response;
    private final OTTViewRepository ottViewRepository;

    @Override
    public void kakaoLogin(SocialLoginType socialLoginType, String code) {
        // 1. 소셜 로그인해서 정보 가져오기
        String userInfo = socialOauth.requestUserInfo(code);

        // 2. 파싱
//        System.out.println("kaKaoUserInfoResponse = " + kaKaoUserInfoResponse.getId());
//        System.out.println("kaKaoUserInfoResponse.getProperties().getNickname() = " + kaKaoUserInfoResponse.getProperties().getNickname());
//        System.out.println("kaKaoUserInfoResponse.getProperties().getProfileImage() = " + kaKaoUserInfoResponse.getProperties().getProfileImage());

        // 공통 유틸로 타입보고 확인해서 아이디 생성기 만들기
//        SocialLoginIdUtil socialLoginIdUtil = new SocialLoginIdUtil(socialLoginType, kaKaoUserInfoResponse.getId());

        // 3. 소셜 로그인 성공한 경우 회원 DB에 최초 가입인지 확인
//        Optional<Member> findMember = memberRepository.findByMemberId(socialLoginIdUtil.getMemberId());

        // 3-1. 최초 로그인이 아닌 경우
//        findMember.ifPresent(member -> {
            /**
             * @return JWTtoken
             */
//        });

        // 3-2. 최초 회원 가입인 경우

        // 2. 소셜 로그인 정보 실패한 경우 에러 던지기 (현홍)
    }

    public void request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        String redirectURL = socialOauth.getOauthRedirectURL();

        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> socialLogin(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        String accessToken = socialOauth.requestAccessToken(code);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> userInfoMap = null;
        try {
            Map<String, String> tokenMap = objectMapper.readValue(accessToken, Map.class);
            String userInfo = socialOauth.requestUserInfo(tokenMap);
            userInfoMap = objectMapper.readValue(userInfo, Map.class);
            System.out.println(userInfoMap.get("id") + " " + userInfoMap.get("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfoMap;
    }

    public SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLogin Tpye 입니다"));
    }

    public Member findByMember_Id(String memberId) {
        return memberRepository.findByMemberId(memberId).orElse(null);
    }

    public Member memberSave(Member member) {
        return memberRepository.save(member);
    }

    public OTTView findByOttId(int ottId) {
        return ottViewRepository.findByOttId(ottId);
    }
}
