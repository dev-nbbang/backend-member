package com.dev.nbbang.member.domain.user.service;


import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.dto.response.MemberResponse;
import com.dev.nbbang.member.domain.user.dto.response.NicknameMemberResponse;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;

import java.util.Optional;

public interface MemberService {
    // 소셜 로그인 시도
    String socialLogin(SocialLoginType socialLoginType, String code);

    // 아이디로 회원 찾기
    MemberResponse findMember(String memberId);

    // 중복 닉네임 찾기
    NicknameMemberResponse findMemberByNickname(String nickname);

    // 회원 추가 정보 저장
    Member memberSave(Member member);

    OTTView findByOttId(int ottId);

}
