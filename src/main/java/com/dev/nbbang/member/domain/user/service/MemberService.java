package com.dev.nbbang.member.domain.user.service;


import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;

import java.util.List;

public interface MemberService {
    // 아이디로 회원 찾기
    MemberDTO findMember(String memberId);

    // 닉네임으로 회원 찾기
    MemberDTO findMemberByNickname(String nickname);

    // 회원 프로필 수정
    MemberDTO updateMember(String memberId, Member member, List<Integer> ottId);

    // 닉네임 중복 체크
    boolean duplicateNickname(String nickname);

    // 비슷한 닉네임 리스트 가져오기
    List<MemberDTO> findMemberListByNickname(String nickname);

    // 회원 탈퇴
    void deleteMember(String memberId);

    // 로그 아웃
    boolean logout(String memberId);

    // 등급 수정
    MemberDTO updateGrade(String memberId, Member member);

    // 경험치 변경
    MemberDTO updateExp(String memberId, Member member);

    // 회원 계좌 정보 저장
    void updateAccount(String memberId, Member member);

    // 회원 계좌 정보 삭제
    void deleteAccount(String memberId);

    // 회원 빌링키 정보 저장
    void updateBillingKey(String memberId, String billingKey);

    // 회원 빌링키 정보 삭제
    void deleteBillingKey(String memberId);
}
