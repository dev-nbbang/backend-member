package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 레포지토리 : 회원 아이디로 회원 찾기")
    void 회원아이디로_회원_찾기_성공() {
        //given

        //when

        //then

    }

    @Test
    @DisplayName("회원 레포지토리 : 회원가입, 추가 회원정보 저장/관심 OTT 있는 경우")
    void 회원가입_추가_회원정보_저장_관심OTT_있는_경우_성공() {
        //given
        List<OTTView> ottViewList = new ArrayList<>();
        ottViewList.add(OTTView.builder().ottId(1).build());
        ottViewList.add(OTTView.builder().ottId(2).build());

        final Member member = Member.builder()
                .memberId("TestId")
                .nickname("TestMemberA")
                .ottView(ottViewList).build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        assertThat(savedMember.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(savedMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(savedMember.getOttView().size()).isEqualTo(member.getOttView().size());
        assertThat(savedMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(savedMember.getPoint()).isEqualTo(0);
        assertThat(savedMember.getExp()).isEqualTo(0);
    }
}