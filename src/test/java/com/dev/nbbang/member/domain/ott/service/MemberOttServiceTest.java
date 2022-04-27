package com.dev.nbbang.member.domain.ott.service;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchMemberOttException;
import com.dev.nbbang.member.domain.ott.repository.MemberOttRepository;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberOttServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberOttRepository memberOttRepository;

    @Mock
    private OttViewRepository ottViewRepository;

    @InjectMocks
    private MemberOttServiceImpl memberOttService;

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 등록 성공")
    void 관심_OTT_등록_성공() {
        /**
         * GIVEN
         * 1. 회원 아이디로 회원 찾기
         * 2. OTT 아이디로 OTT 찾기
         * 3. 관심 OTT 서비스가 한 개라도 등록되어 있는 경우 관심 OTT 모두 삭제
         * 4. 새로운 관심 OTT 서비스 모두 저장
         */
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(ottViewRepository.findAllByOttIdIn(anyList())).willReturn(testOttView());
        given(memberOttRepository.findAllByMember(any())).willReturn(testMemberOtt());
        given(memberOttRepository.saveAll(anyList())).willReturn(testSaveMemberOtt());

        // when
        List<MemberOttDTO> savedMemberOtt = memberOttService.saveMemberOtt("Test Id", new ArrayList<Integer>(Arrays.asList(2, 3)));

        // then
        assertEquals(savedMemberOtt.size(), 2);
        assertEquals(savedMemberOtt.get(0).getMember().getMemberId(), "Test Id");
        assertEquals(savedMemberOtt.get(0).getMember().getNickname(), "Test Nickname");
        assertEquals(savedMemberOtt.get(0).getOttView().getOttId(), 2);
        assertEquals(savedMemberOtt.get(0).getOttView().getOttName(), "test2");
        assertEquals(savedMemberOtt.get(0).getOttView().getOttImage(), "test2.image");
        assertEquals(savedMemberOtt.get(1).getOttView().getOttId(), 3);
        assertEquals(savedMemberOtt.get(1).getOttView().getOttName(), "test3");
        assertEquals(savedMemberOtt.get(1).getOttView().getOttImage(), "test3.image");
    }

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 등록 실패")
    void 관심_OTT_등록_실패() {
        /**
         * GIVEN
         * 1. 회원 아이디로 회원 찾기
         * 2. OTT 아이디로 OTT 찾기
         * 3. 관심 OTT 서비스가 한 개라도 등록되어 있는 경우 관심 OTT 모두 삭제
         * 4. 새로운 관심 OTT 서비스 모두 저장 (예외 발생)
         */
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(ottViewRepository.findAllByOttIdIn(anyList())).willReturn(testOttView());
        given(memberOttRepository.findAllByMember(any())).willReturn(testMemberOtt());
        given(memberOttRepository.saveAll(anyList())).willThrow(NoCreatedMemberOttException.class);

        // then
        assertThrows(NoCreatedMemberOttException.class, () -> memberOttService.saveMemberOtt("Test Id", new ArrayList<>(Arrays.asList(2, 3))));
    }

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 조회 성공")
    void 관심_OTT_조회_성공() {
        /**
         * GIVEN
         * 1. 회원 아이디로 회원 찾기
         * 2. 찾은 회원으로 등록된 관심 OTT 모두 조회
         */
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(memberOttRepository.findAllByMember(any())).willReturn(testMemberOtt());

        // when
        List<MemberOttDTO> findMemberOtt = memberOttService.findMemberOttByMemberId("Test Id");

        // then
        assertEquals(findMemberOtt.size(), 1);
        assertEquals(findMemberOtt.get(0).getMember().getMemberId(), "Test Id");
        assertEquals(findMemberOtt.get(0).getMember().getNickname(), "Test Nickname");
        assertEquals(findMemberOtt.get(0).getOttView().getOttId(),1);
        assertEquals(findMemberOtt.get(0).getOttView().getOttName(),"test");
        assertEquals(findMemberOtt.get(0).getOttView().getOttImage(),"test.image");
    }

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 조회 실패")
    void 관심_OTT_조회_실패() {
        /**
         * GIVEN
         * 1. 회원 아이디로 회원 찾기
         * 2. 찾은 회원으로 등록된 관심 OTT 모두 조회 (예외 발생)
         */
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(memberOttRepository.findAllByMember(any())).willThrow(NoSuchMemberOttException.class);

        // then
        assertThrows(NoSuchMemberOttException.class, () -> memberOttService.findMemberOttByMemberId("Test Id"));
    }

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 전체 삭제 성공")
    void 관심_OTT_전체_삭제_성공() {
        /**
         * GIVEN
         * 1. 회원 아이디로 회원 찾기
         * 2. 찾은 회원을 통해 관심 OTT 등록 확인 후 전체 삭제
         */
        Member testMember = testMember();
        List<MemberOtt> memberOttList = testMemberOtt();

        given(memberRepository.findByMemberId(anyString())).willReturn(testMember);
        given(memberOttRepository.findAllByMember(any())).willReturn(memberOttList);

        // when
        memberOttService.deleteAllMemberOtt("Test Id");

        // then
        verify(memberOttRepository, times(1)).deleteByMember(testMember);
    }

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 전체 삭제 실패")
    void 관심_OTT_전체_삭제_실패() {
        /**
         * GIVEN
         * 1. 회원 아이디로 회원 찾기
         * 2. 찾은 회원을 통해 관심 OTT 등록 확인 후 전체 삭제 (예외 발생)
         */
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(memberOttRepository.findAllByMember(any())).willThrow(NoSuchMemberOttException.class);

        // then
        assertThrows(NoSuchMemberOttException.class, () -> memberOttService.deleteAllMemberOtt("Test Id"));
    }

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 한개 삭제 성공")
    void 관심_OTT_한개_삭제_성공() {
        /**
         * 1. 회원 아이디로 회원 찾기
         * 2. OTT 아이디로 OTT 찾기
         * 3. 찾은 회원과 찾은 OTT를 통해 관심 OTT 등록 확인 후 삭제 처리
         */
        Member testMember = testMember();
        OttView testOttView = testOttViewOnce();
        MemberOtt testMemberOtt = testMemberOttOnce();

        given(memberRepository.findByMemberId(anyString())).willReturn(testMember);
        given(ottViewRepository.findByOttId(anyInt())).willReturn(testOttView);
        given(memberOttRepository.findMemberOttByMemberAndOttView(any(), any())).willReturn(testMemberOtt);

        // when
        memberOttService.deleteMemberOtt("Test Id", 1);

        // then
        verify(memberOttRepository, times(1)).deleteByMemberAndOttView(testMember, testOttView);
    }

    @Test
    @DisplayName("관심 OTT 서비스 : 관심 OTT 한개 삭제 실패")
    void 관심_OTT_한개_삭제_실패() {
        /**
         * 1. 회원 아이디로 회원 찾기
         * 2. OTT 아이디로 OTT 찾기
         * 3. 찾은 회원과 찾은 OTT를 통해 관심 OTT 등록 확인 후 삭제 처리 (예외 발생)
         */
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(ottViewRepository.findByOttId(anyInt())).willReturn(testOttViewOnce());
        given(memberOttRepository.findMemberOttByMemberAndOttView(any(), any())).willThrow(NoSuchMemberOttException.class);

        // then
        assertThrows(NoSuchMemberOttException.class, () -> memberOttService.deleteMemberOtt("Test Id", 1));
    }

    /**
     * 테스트 객체 생성
     */
    private static Member testMember() {
        return Member.builder()
                .memberId("Test Id")
                .nickname("Test Nickname")
                .point(0L)
                .exp(0L)
                .grade(Grade.BRONZE)
                .memberOtt(testMemberOtt())
                .partyInviteYn("Y")
                .build();
    }

    private static List<MemberOtt> testMemberOtt() {
        List<MemberOtt> memberOtt = new ArrayList<>();
        Member testMember = Member.builder().memberId("Test Id")
                .nickname("Test Nickname")
                .grade(Grade.BRONZE)
                .exp(0L)
                .point(0L)
                .partyInviteYn("Y")
                .build();

        memberOtt.add(MemberOtt.builder().member(testMember).ottView(new OttView(1, "test", "test.image")).build());

        return memberOtt;
    }

    private static List<OttView> testOttView() {
        List<OttView> ottView = new ArrayList<>();
        ottView.add(new OttView(2, "test2", "test2.image"));
        ottView.add(new OttView(3, "test3", "test3.image"));

        return ottView;
    }

    private static List<MemberOtt> testSaveMemberOtt() {
        List<MemberOtt> memberOtt = new ArrayList<>();
        memberOtt.add(MemberOtt.builder().member(testMember()).ottView(testOttView().get(0)).build());
        memberOtt.add(MemberOtt.builder().member(testMember()).ottView(testOttView().get(1)).build());

        return memberOtt;
    }

    private static OttView testOttViewOnce() {
        return OttView.builder().ottId(1).ottName("test").ottName("test.image").build();
    }

    private static MemberOtt testMemberOttOnce() {
        Member testMember = Member.builder().memberId("Test Id")
                .nickname("Test Nickname")
                .grade(Grade.BRONZE)
                .exp(0L)
                .point(0L)
                .partyInviteYn("Y")
                .build();

        return MemberOtt.builder().member(testMember).ottView(new OttView(1, "test", "test.image")).build();
    }
}
