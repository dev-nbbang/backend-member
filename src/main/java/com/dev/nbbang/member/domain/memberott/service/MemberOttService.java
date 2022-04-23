package com.dev.nbbang.member.domain.memberott.service;

import com.dev.nbbang.member.domain.memberott.dto.MemberOttDTO;

import java.util.List;
import java.util.Set;

public interface MemberOttService {
    // 관심 OTT 등록
    List<MemberOttDTO> saveMemberOtt(String memberId, List<Integer> ottId);

    // 관심 OTT 해제
}
