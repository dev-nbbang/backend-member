package com.dev.nbbang.member.domain.memberott.repository;

import com.dev.nbbang.member.domain.memberott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLIntegrityConstraintViolationException;

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
        MemberOtt memberOtt = testMemberOtt();

        //when
        MemberOtt savedMemberOtt = memberOttRepository.save(memberOtt);

        //then
        assertThat(savedMemberOtt.getMember()).isInstanceOf(Member.class);
        assertThat(savedMemberOtt.getMember().getMemberId()).isEqualTo("1");
        assertThat(savedMemberOtt.getOttView().getOttId()).isEqualTo(1);
        assertThat(savedMemberOtt.getMember().getNickname()).isEqualTo("test A");
        assertThat(savedMemberOtt.getOttView().getOttName()).isEqualTo("test");
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

    private static MemberOtt testMemberOtt(){
        Member member = Member.builder()
                .memberId("1")
                .nickname("test A")
                .grade(Grade.BRONZE)
                .point(0L)
                .exp(0L)
                .partyInviteYn("Y").build();
        OttView ottView = OttView.builder()
                .ottId(1)
                .ottName("test")
                .ottImage("test.com")
                .build();

        return MemberOtt.builder()
                .member(member)
                .ottView(ottView)
                .build();
    }
}