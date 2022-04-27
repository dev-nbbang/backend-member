package com.dev.nbbang.member.domain.ott.repository;

import com.dev.nbbang.member.domain.ott.entity.OttView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OttViewRepositoryTest {
    @Autowired
    private OttViewRepository ottViewRepository;

    @Test
    @DisplayName("OttView 레포지토리 : OTT 아이디를 통해서 OTT 서비스 찾기 성공")
    void OTT_서비스_찾기_성공() {
        // given
        Integer ottId = 1;
        OttView ottView = testOttView();

        // when
        OttView findOttView = ottViewRepository.findByOttId(ottId);

        // then
        assertEquals(findOttView.getOttId(),ottView.getOttId());
        assertEquals(findOttView.getOttName(),ottView.getOttName());
        assertEquals(findOttView.getOttImage(),ottView.getOttImage());
    }

    @Test
    @DisplayName("OttView 레포지토리 : OTT 아이디를 통해서 OTT 서비스 찾기 실패")
    void OTT_서비스_찾기_실패() {
        // given
        Integer ottId = 4;

        // when
        OttView findOttView = ottViewRepository.findByOttId(ottId);

        // then
        assertThat(findOttView).isNull();
    }

    @Test
    @DisplayName("OttView 레포지토리 : OTT 아이디 리스트를 통해 한번에 찾기 성공")
    void OTT_서비스_리스트_찾기_성공() {
        //given
        List<Integer> ottId = new ArrayList<>(Arrays.asList(1, 2));
        List<OttView> ottView = testOttViewList();

        // when
        List<OttView> findOttView = ottViewRepository.findAllByOttIdIn(ottId);

        // then
        assertEquals(findOttView.size(),2);
        assertEquals(findOttView.get(0).getOttId(),ottView.get(0).getOttId());
        assertEquals(findOttView.get(0).getOttName(),ottView.get(0).getOttName());
        assertEquals(findOttView.get(0).getOttImage(),ottView.get(0).getOttImage());
        assertEquals(findOttView.get(1).getOttId(),ottView.get(1).getOttId());
        assertEquals(findOttView.get(1).getOttName(),ottView.get(1).getOttName());
        assertEquals(findOttView.get(1).getOttImage(),ottView.get(1).getOttImage());
    }

    @Test
    @DisplayName("OttView 레포지토리 : OTT 아이디 리스트를 통해 한번에 찾기 실패 (모두 없는 경우)")
    void OTT_서비스_리스트_찾기_실패() {
        // given
        List<Integer> ottId = new ArrayList<>(Arrays.asList(4, 5));

        // when
        List<OttView> findOttView = ottViewRepository.findAllByOttIdIn(ottId);

        // then
        assertEquals(findOttView.size(), 0);
        assertThat(findOttView).isEqualTo(Collections.emptyList());
        assertThat(findOttView.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("OttView 레포지토리 : OTT 서비스 전체 조회 성공")
    void OTT_서비스_리스트_전체_조회_성공() {
        // when
        List<OttView> findOttView = ottViewRepository.findAll();

        //then
        assertEquals(findOttView.size(), 3);
    }

    private static OttView testOttView() {
        return OttView.builder().ottId(1).ottName("test").ottImage("test.com").build();
    }

    private static List<OttView> testOttViewList() {
        List<OttView> ottViewList = new ArrayList<>();
        ottViewList.add(OttView.builder().ottId(1).ottName("test").ottImage("test.com").build());
        ottViewList.add(OttView.builder().ottId(2).ottName("test2").ottImage("test2.com").build());

        return ottViewList;
    }
}
