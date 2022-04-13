package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.api.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.SocialOauth;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.user.repository.OTTViewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final List<SocialOauth> socialOauthList;
    private final HttpServletResponse response;

    @Autowired
    public MemberRepository memberRepository;
    @Autowired
    public OTTViewRepository ottViewRepository;

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

    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
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
