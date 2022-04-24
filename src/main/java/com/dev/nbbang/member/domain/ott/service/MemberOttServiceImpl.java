package com.dev.nbbang.member.domain.ott.service;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOtt;
import com.dev.nbbang.member.domain.ott.exception.NoSuchMemberOttException;
import com.dev.nbbang.member.domain.ott.repository.MemberOttRepository;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
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
        Optional<List<MemberOtt>> savedMemberOtt = Optional.of(memberOttRepository.saveAll(MemberOttDTO.toEntityList(findMember, findOttViews)));

        return MemberOttDTO.createList(savedMemberOtt.orElseThrow(() -> new NoCreatedMemberOtt("관심 OTT 서비스가 등록되지 않았습니다.", NbbangException.NO_CREATE_MEMBER_OTT)));
    }

    @Override
    public List<MemberOttDTO> findMemberOttByMemberId(String memberId) {
        // 1. 회원 찾기
        Member findMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        // 2. 찾은 회원을 통해 MemberOtt 찾기
        List<MemberOtt> findMemberOtt = memberOttRepository.findAllByMember(findMember).orElseThrow(() -> new NoSuchMemberOttException("등록된 관심 OTT 서비스가 없습니다.", NbbangException.NOT_FOUND_MEMBER_OTT));

        return MemberOttDTO.createList(findMemberOtt);
    }

    @Override
    @Transactional
    public void deleteAllMemberOtt(String memberId) {
        // 1. 회원 찾기
        Member findMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        // 2. 찾은 회원을 통해 Member OTT 삭제 (예외처리)
        memberOttRepository.deleteByMember(findMember);
    }

    @Override
    @Transactional
    public void deleteMemberOtt(String memberId, Integer ottId) {
        // 1. 회원 찾기
        Member findMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        // 2. Ott 서비스 찾기
        OttView findOttView = ottViewRepository.findByOttId(ottId).orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 플랫폼입니다.", NbbangException.NOT_FOUND_OTT));

        // 3. 찾은 회원 및 Ott 서비스를 통해 Member Ott 삭제 (예외처리)
        memberOttRepository.deleteByMemberAndOttView(findMember, findOttView);
    }
}
