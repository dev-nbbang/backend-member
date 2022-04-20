package com.dev.nbbang.member.domain.user.service;


import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;

import java.util.List;

public interface MemberService {
    // 소셜 로그인 시도
    String socialLogin(SocialLoginType socialLoginType, String code);

    // 아이디로 회원 찾기
    MemberDTO findMember(String memberId);

    // 닉네임으로 회원 찾기
    MemberDTO findMemberByNickname(String nickname);

    // 회원 추가 정보 저장
    MemberDTO memberSave(Member member);

    // 회원 정보 수정
    MemberDTO updateMember(String memberId, Member member);

    // 회원 계좌 정보 저장
    void updateAccount(String memberId, Member member);

    // 회원 계좌 정보 삭제
    void deleteAccount(String memberId);

    // 회원 빌링키 정보 저장
    void updateBillingKey(String memberId, String billingKey);

    // 회원 빌링키 정보 삭제
    void deleteBillingKey(String memberId);

    // 닉네임 중복 체크
    boolean duplicateNickname(String nickname);

    // 비슷한 닉네임 리스트 가져오기
    List<MemberDTO> findMemberListByNickname(String nickname);

    // 회원 탈퇴
    void deleteMember(String memberId);

    // 로그 아웃
    boolean logout(String memberId);

    OTTView findByOttId(int ottId);

}
