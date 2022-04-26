package com.dev.nbbang.member.domain.ott.service;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;

import java.util.List;

public interface MemberOttService {
    // 관심 OTT 등록
    List<MemberOttDTO> saveMemberOtt(String memberId, List<Integer> ottId);

    // 관심 OTT 조회
    List<MemberOttDTO> findMemberOttByMemberId(String memberId);

    // 회원 아이디로 관심 OTT 전체 삭제
    void deleteAllMemberOtt(String memberId);

    // 회원 아이디, 관심 OTT 아이디로 삭제
    void deleteMemberOtt(String memberId, Integer ottId);
}
