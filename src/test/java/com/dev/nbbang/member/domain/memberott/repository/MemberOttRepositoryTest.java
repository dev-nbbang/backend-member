package com.dev.nbbang.member.domain.memberott.repository;

import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.repository.MemberOttRepository;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberOttRepositoryTest {
    @Autowired
    private MemberOttRepository memberOttRepository;

    @Test
    @DisplayName("관심 OTT 레포지토리 : 관심 OTT 저장 성공")
    void 관심_OTT_저장_성공() {
        //given
        List<MemberOtt> memberOtt = testMemberOtt();

        //when
        List<MemberOtt> savedMemberOtt = memberOttRepository.saveAll(memberOtt);

        //then
        assertThat(savedMemberOtt.get(0).getMember()).isInstanceOf(Member.class);
        assertThat(savedMemberOtt.get(0).getOttView()).isInstanceOf(OttView.class);
        assertThat(savedMemberOtt.get(0).getMember().getMemberId()).isEqualTo("test424");
        assertThat(savedMemberOtt.get(0).getMember().getNickname()).isEqualTo("맹준영수정");
        assertThat(savedMemberOtt.get(0).getOttView().getOttId()).isEqualTo(1);
        assertThat(savedMemberOtt.get(0).getOttView().getOttName()).isEqualTo("test");
        assertThat(savedMemberOtt.get(0).getOttView().getOttImage()).isEqualTo("test.com");
    }

    @Test
    @DisplayName("관심 OTT 레포지토리 : 관심 OTT 저장 실패")
    void 관심_OTT_저장_실패() {
        //given
        MemberOtt memberOtt = MemberOtt.builder().member(Member.builder().memberId("new").nickname("new").build())
                .ottView(OttView.builder().ottId(2).build()).build();

        //then
        assertThrows(DataIntegrityViolationException.class, () -> memberOttRepository.save(memberOtt));
    }

    @Test
    @DisplayName("관심 OTT 레포지토리 : 회원 아이디로 관심 OTT 조회 성공")
    void 관심_OTT_조회_성공() {
        // given
        Member member = testMember();

        // when
        List<MemberOtt> findMemberOtt = memberOttRepository.findAllByMember(member);

        // then
        assertThat(findMemberOtt.size()).isEqualTo(2);
        assertThat(findMemberOtt.get(0).getMember().getMemberId()).isEqualTo("test424");
        assertThat(findMemberOtt.get(0).getMember().getNickname()).isEqualTo("맹준영수정");
        assertThat(findMemberOtt.get(0).getOttView().getOttName()).isEqualTo("test2");
        assertThat(findMemberOtt.get(0).getOttView().getOttImage()).isEqualTo("test2.com");
        assertThat(findMemberOtt.get(1).getOttView().getOttName()).isEqualTo("test3");
        assertThat(findMemberOtt.get(1).getOttView().getOttImage()).isEqualTo("test3.com");
    }

    @Test
    @DisplayName("관심 OTT 레포지토리 : 회원 아이디로 관심 OTT 조회 실패")
    void 관심_OTT_조회_실패() {
        // given
        Member member = Member.builder().memberId("new").nickname("new").build();

        //then
        List<MemberOtt> findMemberOtt = memberOttRepository.findAllByMember(member);

        //when
        assertThat(findMemberOtt.isEmpty()).isTrue();
        assertThat(findMemberOtt).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("관심 OTT 레포지토리 : 회원 아이디로 관심 OTT 전체 삭제 성공")
    void 관심_OTT_전체_삭제_성공() {
        // given
        Member member = testMember();

        // when
        memberOttRepository.deleteByMember(member);
        List<MemberOtt> findMemberOtt = memberOttRepository.findAllByMember(member);

        // then
        assertThat(findMemberOtt.isEmpty()).isTrue();
        assertThat(findMemberOtt).isEqualTo(Collections.emptyList());
    }


    @Test
    @DisplayName("관심 OTT 레포지토리 : 회원 아이디로 관심 OTT 한 개 삭제 성공")
    void 관심_OTT_한개_삭제_성공() {
        // given
        Member member = testMember();
        OttView ottView = OttView.builder()
                .ottId(2)
                .ottName("test2")
                .ottImage("test2.com")
                .build();

        // when
        memberOttRepository.deleteByMemberAndOttView(member, ottView);
        List<MemberOtt> findMemberOtt = memberOttRepository.findAllByMember(member);

        //then
        assertThat(findMemberOtt.isEmpty()).isFalse();
        assertThat(findMemberOtt.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("관심 OTT 레포지토리 : 회원 아이디, Ott ID로 관심 OTT 등록여부 판단 데이터 없음")
    void 관심_OTT_등록여부_판단_성공() {
        // given
        Member member = testMember();
        OttView ottView = OttView.builder()
                .ottId(1)
                .ottName("test1")
                .ottImage("test1.com")
                .build();

        // when
        MemberOtt findMemberOtt = memberOttRepository.findMemberOttByMemberAndOttView(member, ottView);

        //then
        assertThat(findMemberOtt).isNull();
    }

    @Test
    @DisplayName("관심 OTT 레포지토리 : 회원 아이디, Ott ID로 관심 OTT 등록여부 판단 데이터 있음")
    void 관심_OTT_등록여부_판단_실패() {
        // given
        Member member = testMember();
        OttView ottView = OttView.builder()
                .ottId(2)
                .ottName("test2")
                .ottImage("test2.com").build();

        // when
        MemberOtt findMemberOtt = memberOttRepository.findMemberOttByMemberAndOttView(member, ottView);     //npe

        //then
        assertThat(findMemberOtt).isNotNull();
        assertThat(findMemberOtt.getMember().getMemberId()).isEqualTo("test424");
        assertThat(findMemberOtt.getOttView().getOttId()).isEqualTo(2);
    }

    private static List<MemberOtt> testMemberOtt(){
        List<MemberOtt> memberOtt = new ArrayList<>();
        Member member = testMember();
        OttView ottView = testOttView();

        memberOtt.add(MemberOtt.builder().member(member).ottView(ottView).build());

        return memberOtt;
    }

    private static Member testMember() {
        return Member.builder()
                .memberId("test424")
                .nickname("맹준영수정")
                .grade(Grade.BRONZE)
                .point(0L)
                .exp(0L)
                .partyInviteYn("Y").build();
    }

    private static OttView testOttView() {
        return OttView.builder()
                .ottId(1)
                .ottName("test")
                .ottImage("test.com")
                .build();
    }
}