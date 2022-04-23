package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 레포지토리 : 회원 아이디로 회원 찾기 - 성공")
    void 회원아이디로_회원_찾기_성공() {
        //given
        final Member member = Member.builder()
                .memberId("TestIds")
                .nickname("TestMemberA")
                .build();
        Member savedMember = memberRepository.save(member);

        //when
        Member findMember = memberRepository.findByMemberId("TestIds").orElseGet(null);

        //then

        assertThat(savedMember.getMemberId()).isEqualTo(findMember.getMemberId());
        assertThat(savedMember.getNickname()).isEqualTo(findMember.getNickname());
    }

    @Test
    @DisplayName("회원 레포지토리 : 회원 아이디로 회원 찾기 - 실패")
    void 회원아이디로_회원_찾기_실패() {
        Optional<Member> savedMember = memberRepository.findByMemberId("test");

        //then
        assertThat(savedMember).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("회원 레포지토리 : 회원 닉네임으로 회원 찾기 - 성공")
    void 회원_닉네임으로_회원_찾기_성공() {
        //given
        final Member member = Member.builder()
                .memberId("TestIds")
                .nickname("TestMemberA")
                .build();
        Member savedMember = memberRepository.save(member);

        //when
        Member findMember = memberRepository.findByNickname("TestMemberA").orElseGet(null);

        //then
        assertThat(savedMember.getMemberId()).isEqualTo(findMember.getMemberId());
        assertThat(savedMember.getNickname()).isEqualTo(findMember.getNickname());
    }

    @Test
    @DisplayName("회원 레포지토리 : 회원 닉네임으로 회원 찾기 - 실패")
    void 회원_닉네임으로_회원_찾기_실패() {
        //when
        Optional<Member> member = memberRepository.findByNickname("not nickname");

        //then
        assertThat(member).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("회원 레포지토리 : 회원가입, 추가 회원정보 저장/관심 OTT 있는 경우 - 성공")
    void 회원가입_추가_회원정보_저장_관심OTT_있는_경우_성공() {
        //given
        List<OttView> ottViewList = new ArrayList<>();
        ottViewList.add(OttView.builder().ottId(1).build());

        final Member member = Member.builder()
                .memberId("TestIds")
                .nickname("TestMemberA")
                .build();
//                .ottView(ottViewList).build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        assertThat(savedMember.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedMember.getNickname()).isEqualTo(member.getNickname());
//        assertThat(savedMember.getOttView().size()).isEqualTo(member.getOttView().size());
        assertThat(savedMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(savedMember.getPoint()).isEqualTo(0);
        assertThat(savedMember.getExp()).isEqualTo(0);
        assertThat(savedMember.getPartyInviteYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("회원 레포지토리 : 회원가입, 추가 회원정보 저장/관심 OTT 없는 경우 - 성공")
    void 회원가입_추가_회원정보_저장_관심OTT_없는_경우_성공() {
        //given
        final Member member = Member.builder()
                .memberId("Test Id")
                .nickname("맹준").build();
//                .ottView(new ArrayList<>())
//                .build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        assertThat(savedMember.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(savedMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(savedMember.getPoint()).isEqualTo(0);
        assertThat(savedMember.getExp()).isEqualTo(0);
        assertThat(savedMember.getPartyInviteYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("회원 레포지토리 : 닉네임으로 비슷하 회원 리스트 가져오기 - 성공")
    void 닉네임으로_비슷한_회원_리스트_가져오기_성공() {
        // given
        String nickname = "test";

        // when
        List<Member> members = memberRepository.findTop5ByNicknameStartingWith(nickname).orElseGet(ArrayList::new);

        // then
        for (Member member : members) {
            assertThat(member.getNickname()).contains(nickname);
        }
    }

    @Test
    @DisplayName("회원 레포지토리 : 닉네임으로 비슷한 회원 리스트 가져오기 - 실패")
    void 닉네임으로_비슷한_회원_리스트_가져오기_실패() {
        // given
        String nickname = "none";

        // when
        List<Member> members = memberRepository.findTop5ByNicknameStartingWith(nickname).orElseGet(ArrayList::new);

        // then
        assertThat(members.size()).isEqualTo(0);
        assertThat(members.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("회원 레포지토리 : 회원 아이디로 회원 삭제하기 - 성공")
    void 회원_아이디로_회원_삭제하기_성공() {
        // given
        String memberId = "1";

        // when
        memberRepository.deleteByMemberId(memberId);
        Optional<Member> findMember = memberRepository.findByMemberId(memberId);

        // then
        assertThat(findMember).isEqualTo(Optional.empty());
    }
}