package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.KakaoOauth;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원 서비스 : 아이디로 회원 찾기 - 성공")
    void 아이디로_회원_찾기_성공() {
        //given
        given(memberRepository.findByMemberId(anyString())).willReturn(Optional.of(testMemberBuilder()));

        //when
        MemberDTO findMember = memberService.findMember("Test Id");

        //then
        assertThat(findMember.getMemberId()).isEqualTo("Test Id");
        assertThat(findMember.getNickname()).isEqualTo("Test Nickname");
        assertThat(findMember.getPoint()).isEqualTo(0L);
        assertThat(findMember.getExp()).isEqualTo(0L);
        assertThat(findMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(findMember.getPartyInviteYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("회원 서비스 : 아이디로 회원 찾기 - 실패")
    void 아이디로_회원_찾기_실패() {
        //given
        given(memberRepository.findByMemberId(anyString())).willReturn(Optional.empty());

        //when
        assertThrows(NoSuchMemberException.class, () -> memberService.findMember("Test Id"), "회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 닉네임으로 회원 찾기 - 성공")
    void 닉네임으로_회원_찾기_성공() {
        //given
        given(memberRepository.findByNickname(anyString())).willReturn(Optional.of(testMemberBuilder()));

        //when
        MemberDTO findMember = memberService.findMemberByNickname("Test Nickname");

        //then
        assertThat(findMember.getMemberId()).isEqualTo("Test Id");
        assertThat(findMember.getNickname()).isEqualTo("Test Nickname");
        assertThat(findMember.getPoint()).isEqualTo(0L);
        assertThat(findMember.getExp()).isEqualTo(0L);
        assertThat(findMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(findMember.getPartyInviteYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("회원 서비스 : 닉네임으로 회원 찾기 - 실패")
    void 닉네임으로_회원_찾기_실패() {
        //given
        given(memberRepository.findByNickname(anyString())).willReturn(Optional.empty());

        //then
        assertThrows(NoSuchMemberException.class, () -> memberService.findMemberByNickname("Test Nickname"), "회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 회원 추가 정보 저장 - 성공")
    void 회원_추가_정보_저장_성공() {
        //given
        given(memberRepository.save(any())).willReturn(testMemberBuilder());

//        when
        MemberDTO savedMember = memberService.memberSave(testMemberBuilder());

        //then
        assertThat(savedMember.getMemberId()).isEqualTo("Test Id");
        assertThat(savedMember.getNickname()).isEqualTo("Test Nickname");
        assertThat(savedMember.getPoint()).isEqualTo(0L);
        assertThat(savedMember.getExp()).isEqualTo(0L);
        assertThat(savedMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(savedMember.getPartyInviteYn()).isEqualTo("Y");

    }

    @Test
    @DisplayName("회원 서비스 : 회원 추가 정보 저장 - 실패")
    void 회원_추가_정보_저장_실패() {
        //given
        given(memberRepository.save(any())).willReturn(null);

        // when
        assertThrows(NoCreateMemberException.class, () -> memberService.memberSave(testMemberBuilder()), "회원가입에 싶패했습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 닉네임 중복 체크 - 성공")
    void 닉네임_중복_체크_성공() {
        // given
        given(memberRepository.findByNickname(anyString())).willReturn(Optional.of(testMemberBuilder()));

        //when
        boolean isDuplicate = memberService.duplicateNickname("Test Nickname");

        assertThat(isDuplicate).isTrue();
    }

    @Test
    @DisplayName("회원 서비스 : 닉네임 중복 체크 - 실패")
    void 닉네임_중복_체크_실패() {
        // given
        given(memberRepository.findByNickname(anyString())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchMemberException.class, () -> memberService.duplicateNickname("Test Nickname"), "회원이 존재하지 않습니다.");
    }

    private static Member testMemberBuilder() {
        return Member.builder()
                .memberId("Test Id")
                .nickname("Test Nickname")
                .point(0L)
                .exp(0L)
                .grade(Grade.BRONZE.name())
                .partyInviteYn("Y").build();
    }

    @Test
    @DisplayName("회원 서비스 : 비슷한 닉네임을 가지 회원 리스트 가져오기 - 성공")
    void 비슷한_닉네임을_가진_회원_리스트_가져오기_성공() {
        // given
//        given(memberRepository.findTop5ByNicknameStartingWith(anyString()))
    }
}