package com.dev.nbbang.member.domain.memberott.service;

import com.dev.nbbang.member.domain.memberott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.memberott.entity.MemberOtt;
import com.dev.nbbang.member.domain.memberott.exception.NoCreatedMemberOtt;
import com.dev.nbbang.member.domain.memberott.repository.MemberOttRepository;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberOttServiceImpl implements MemberOttService{
    private final MemberRepository memberRepository;
    private final MemberOttRepository memberOttRepository;
    private final OttViewRepository ottViewRepository;

    @Override
    @Transactional
    public List<MemberOttDTO> saveMemberOtt(String memberId, List<Integer> ottId) {
        // 1. 회원 찾기
        Member findMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        // 2. OTT 찾기
        List<OttView> findOttViews = ottViewRepository.findAllByOttIdIn(ottId).orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 플랫폼입니다.", NbbangException.NOT_FOUND_OTT));

        // 3. 관심 OTT 등록
        Optional<List<MemberOtt>> savedMemberOtt = Optional.ofNullable(memberOttRepository.saveAll(MemberOttDTO.toEntityList(findMember, findOttViews)));

        return MemberOttDTO.createList(savedMemberOtt.orElseThrow(() -> new NoCreatedMemberOtt("관심 OTT가 등록되지 않았습니다.", NbbangException.NO_CREATE_MEMBER_OTT)));
    }
}
