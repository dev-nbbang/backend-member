package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.DuplicateNicknameException;
import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.domain.user.exception.FailLogoutMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OttViewRepository ottViewRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원 서비스 : 아이디로 회원 찾기 - 성공")
    void 아이디로_회원_찾기_성공() {
        //given
        given(memberRepository.findByMemberId(anyString())).willReturn(testMemberBuilder());

        //when
        MemberDTO findMember = memberService.findMember("Test Id");

        //then
        assertThat(findMember.getMemberId()).isEqualTo("Test Id");
        assertThat(findMember.getNickname()).isEqualTo("Test Nickname");
        assertThat(findMember.getPoint()).isEqualTo(0L);
        assertThat(findMember.getExp()).isEqualTo(0L);
        assertThat(findMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(findMember.getPartyInviteYn()).isEqualTo("Y");
        assertThat(findMember.getMemberOtt().get(0).getOttView().getOttName()).isEqualTo("test");
        assertThat(findMember.getMemberOtt().get(0).getOttView().getOttImage()).isEqualTo("test.image");
    }

    @Test
    @DisplayName("회원 서비스 : 아이디로 회원 찾기 - 실패")
    void 아이디로_회원_찾기_실패() {
        //given
        given(memberRepository.findByMemberId(anyString())).willReturn(null);

        //when
        assertThrows(NoSuchMemberException.class, () -> memberService.findMember("Test Id"), "회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 닉네임으로 회원 찾기 - 성공")
    void 닉네임으로_회원_찾기_성공() {
        //given
        given(memberRepository.findByNickname(anyString())).willReturn(testMemberBuilder());

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
        given(memberRepository.findByNickname(anyString())).willReturn(null);

        //then
        assertThrows(NoSuchMemberException.class, () -> memberService.findMemberByNickname("Test Nickname"), "회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 닉네임 중복 체크 - 성공")
    void 닉네임_중복_체크_성공() {
        // given
        given(memberRepository.findByNickname(anyString())).willReturn(null);

        //when
        boolean isDuplicate = memberService.duplicateNickname("Test Nickname");

        assertThat(isDuplicate).isTrue();
    }

    @Test
    @DisplayName("회원 서비스 : 닉네임 중복 체크 - 실패")
    void 닉네임_중복_체크_실패() {
        // given
        given(memberRepository.findByNickname(anyString())).willReturn(testMemberBuilder());

        // when
        assertThrows(DuplicateNicknameException.class, () -> memberService.duplicateNickname("Test Nickname"), "이미 사용중인 닉네임입니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 비슷한 닉네임을 가지 회원 리스트 가져오기 - 성공")
    void 비슷한_닉네임을_가진_회원_리스트_가져오기_성공() {
        // given
        given(memberRepository.findTop5ByNicknameStartingWith(anyString())).willReturn(testMemberListBuilder());
        String nickname = "Test";

        // when
        List<MemberDTO> members = memberService.findMemberListByNickname(nickname);

        // then
        assertThat(members.size()).isEqualTo(testMemberListBuilder().size());
        for (MemberDTO member : members) {
            assertThat(member.getNickname()).contains(nickname);
        }
    }

    @Test
    @DisplayName("회원 서비스 : 비슷한 닉네임을 가진 회원 리스트 가져오기 - 실패")
    void 비슷한_닉네임을_가진_회원_리스트_가져오기_실패() {
        // given
        given(memberRepository.findTop5ByNicknameStartingWith(anyString())).willReturn(null);
        String nickname = "nothing";

        // then
        assertThrows(NoSuchMemberException.class, () -> memberService.findMemberListByNickname(nickname), "해당 닉네임을 갖는 회원이 존재하지 않습니다.");
    }

    // 레디스 연결 후 테스트
/*    @Test
    @DisplayName("회원 서비스 : 회원 아이디로 회원 탈퇴 - 성공")
    void 회원_아이디로_회원_탈퇴_성공() {
        //given
        String memberId = "test";

        //when
        memberService.deleteMember(memberId);
    }*/

    @Test
    @DisplayName("회원 서비스 : 회원 아이디로 회원 탈퇴 - 실패")
    void 회원_아이디로_회원_탈퇴_실패() {
        // given
        String memberId = "";

        // then
        assertThrows(FailDeleteMemberException.class, () -> memberService.deleteMember(memberId), "회원탈퇴에 실패했습니다.");
    }

    // 레디스 연결 후 테스트
/*    @Test
    @DisplayName("회원 서비스 : 로그아웃 성공")
    void 로그아웃_성공() {
        // given
        String memberId = "K-2197723261";

        // when
        boolean logout = memberService.logout(memberId);

        // then
        assertThat(logout).isTrue();
    }*/
    @Test
    @DisplayName("회원 서비스 : 로그아웃 실패")
    void 로그아웃_실패() {
        // given
        String memberId = "";

        // then
        assertThrows(FailLogoutMemberException.class, () -> memberService.logout(memberId), "로그아웃에 실패했습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 회원 정보 수정 성공")
    void 회원_정보_수정_성공() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(updateMember());
        given(ottViewRepository.findAllByOttIdIn(anyList())).willReturn(updateOttView());


        // when
        MemberDTO updatedMember = memberService.updateMember("Test Id", updateMember(), updateOttId());

        //then
        assertThat(updatedMember.getNickname()).isEqualTo("update nickname");
        assertThat(updatedMember.getPartyInviteYn()).isEqualTo("N");
        assertThat(updatedMember.getExp()).isEqualTo(100L);
        assertThat(updatedMember.getGrade()).isEqualTo(Grade.DIAMOND);
        assertThat(updatedMember.getMemberOtt().get(0).getOttView().getOttName()).isEqualTo("test2");
        assertThat(updatedMember.getMemberOtt().get(0).getOttView().getOttImage()).isEqualTo("test2.image");
        assertThat(updatedMember.getMemberOtt().get(1).getOttView().getOttName()).isEqualTo("test3");
        assertThat(updatedMember.getMemberOtt().get(1).getOttView().getOttImage()).isEqualTo("test3.image");
    }

    @Test
    @DisplayName("회원 서비스 : 회원 정보 수정 실패")
    void 회원_정보_수정_실패() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(null);
        String memberId = "wrong Id";

        // then
        assertThrows(NoSuchMemberException.class, () -> memberService.updateMember(memberId, testMemberBuilder(),updateOttId()), "회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 회원 등급 수정 성공")
    void 회원_등급_수정_성공() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(testMemberBuilder());

        // when
        MemberDTO updatedMember = memberService.updateGrade("Test Id", updateMember());

        //then
        assertThat(updatedMember.getMemberId()).isEqualTo("Test Id");
        assertThat(updatedMember.getGrade()).isEqualTo(Grade.DIAMOND);
    }

    @Test
    @DisplayName("회원 서비스 : 회원 등급 수정 실패")
    void 회원_등급_수정_실패() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(null);
        String memberId = "wrong Id";

        // then
        assertThrows(NoSuchMemberException.class, () -> memberService.updateGrade(memberId, testMemberBuilder()), "회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 서비스 : 회원 경험치 변경 성공")
    void 회원_경험치_변경_성공() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(testMemberBuilder());

        // when
        MemberDTO updatedMember = memberService.updateExp("Test Id", updateMember());

        //then
        assertThat(updatedMember.getMemberId()).isEqualTo("Test Id");
        assertThat(updatedMember.getExp()).isEqualTo(100L);
    }

    @Test
    @DisplayName("회원 서비스 : 회원 경험치 변경 실패")
    void 회원_경험치_변경_실패() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(null);
        String memberId = "wrong Id";

        // then
        assertThrows(NoSuchMemberException.class, () -> memberService.updateExp(memberId, testMemberBuilder()), "회원이 존재하지 않습니다.");
    }

    private static List<Integer> testOttId() {
        List<Integer> ottId = new ArrayList<>();

        ottId.add(1);
        return ottId;
    }

    private static List<Integer> updateOttId() {
        List<Integer> ottId = new ArrayList<>();

        ottId.add(2);
        ottId.add(3);
        return ottId;
    }

    private static List<OttView> testOttView() {
        List<OttView> ottView = new ArrayList<>();
        ottView.add(new OttView(1, "test", "test.image"));
        return ottView;
    }

    private static List<OttView> updateOttView() {
        List<OttView> ottView = new ArrayList<>();

        ottView.add(new OttView(2, "test2", "test2.image"));
        ottView.add(new OttView(3, "test3", "test3.image"));

        return ottView;
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

    private static List<MemberOtt> updatedMemberOtt() {
        List<MemberOtt> memberOtt = new ArrayList<>();
        Member updatedMember = Member.builder().memberId("Test Id")
                .nickname("update Nickname")
                .grade(Grade.DIAMOND)
                .exp(1000L)
                .point(10000L)
                .partyInviteYn("N")
                .build();

        memberOtt.add(MemberOtt.builder().member(updatedMember).ottView(new OttView(2, "test2", "test2.image")).build());
        memberOtt.add(MemberOtt.builder().member(updatedMember).ottView(new OttView(3, "test3", "test3.image")).build());

        return memberOtt;
    }

    private static Member testRecommendMember() {
        return Member.builder()
                .memberId("test")
                .nickname("회사")
                .point(0L)
                .exp(0L)
                .grade(Grade.BRONZE)
                .partyInviteYn("Y")
                .build();
    }

    private static Member testMemberBuilder() {
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

    private static List<Member> testMemberListBuilder() {
        List<Member> members = new ArrayList<>();
        for (int test = 0; test < 3; test++) {
            Member member = Member.builder()
                    .memberId("Test Id" + test)
                    .nickname("Test Nickname" + test)
                    .point(0L)
                    .exp(0L)
                    .grade(Grade.BRONZE)
                    .partyInviteYn("Y").build();
            members.add(member);
        }
        return members;
    }

    private static Member updateMember() {
        return Member.builder()
                .memberId("Test Id")
                .nickname("update nickname")
                .point(0L)
                .exp(100L)
                .grade(Grade.DIAMOND)
                .memberOtt(updatedMemberOtt())
                .partyInviteYn("N").build();
    }

    private static Point testPointBuilder() {
        return Point.builder()
                .member(testRecommendMember())
                .usePoint(500L)
                .pointDetail("test")
                .pointType(PointType.INCREASE)
                .build();

    }
}