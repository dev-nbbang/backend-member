package com.dev.nbbang.member.domain.ott.service;

import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OttViewServiceTest {
    @Mock
    private OttViewRepository ottViewRepository;

    @InjectMocks
    private OttViewServiceImpl ottViewService;

    @Test
    @DisplayName("Ott 서비스 : Ott Id로 특정 Ott 서비스 조회 성공")
    void 특정_Ott_서비스_조회_성공() {
        /**
         * 1. Ott Id로 OTT 서비스 조회
         */
        given(ottViewRepository.findByOttId(anyInt())).willReturn(testOttView());

        // when
        OttViewDTO findOttView = ottViewService.findByOttId(1);

        // then
        assertThat(findOttView.getOttId()).isEqualTo(1);
        assertThat(findOttView.getOttName()).isEqualTo("test");
        assertThat(findOttView.getOttImage()).isEqualTo("test.com");
    }

    @Test
    @DisplayName("Ott 서비스 : Ott Id로 특정 Ott 서비스 조회 실패 ")
    void 특정_Ott_서비스_조회_실패() {
        /**
         * 1. Ott Id로 OTT 서비스 조회 (예외 발생) NoSuchOttException
         */
        given(ottViewRepository.findByOttId(anyInt())).willThrow(NoSuchOttException.class);

        // then
        assertThrows(NoSuchOttException.class, () -> ottViewService.findByOttId(4));

    }

    @Test
    @DisplayName("Ott 서비스 : Ott Id 리스트로 특정 Ott 서비스 리스트 조회 성공")
    void 특정_Ott_서비스_리스트_조회_성공() {
        /**
         * 1. Ott Id 리스트로 OTT 서비스 조회
         */
        given(ottViewRepository.findAllByOttIdIn(anyList())).willReturn(testOttViewList());

        // when
        List<OttViewDTO> findOttView = ottViewService.findAllByOttId(new ArrayList<>(Arrays.asList(1, 2)));

        // then
        assertEquals(findOttView.size(),2);
        assertEquals(findOttView.get(0).getOttId(),1);
        assertEquals(findOttView.get(0).getOttName(),"test");
        assertEquals(findOttView.get(0).getOttImage(),"test.com");
        assertEquals(findOttView.get(1).getOttId(),2);
        assertEquals(findOttView.get(1).getOttName(),"test2");
        assertEquals(findOttView.get(1).getOttImage(),"test2.com");
    }

    @Test
    @DisplayName("Ott 서비스 : Ott Id 리스트로 특정 Ott 서비스 리스트 조회 성공")
    void 특정_Ott_서비스_리스트_조회_실패() {
        /**
         * 1. Ott Id 리스트로 OTT 서비스 조회 (예외 발생) NoSuchOttException
         */
        given(ottViewRepository.findAllByOttIdIn(anyList())).willThrow(NoSuchOttException.class);

        // then
        assertThrows(NoSuchOttException.class, () -> ottViewService.findAllByOttId(new ArrayList<>(Arrays.asList(4,5))));
    }

    @Test
    @DisplayName("Ott 서비스 : Ott 서비스 전체 조회 성공")
    void 특정_Ott_서비스_전체_조회_성공() {
        /**
         * 1. OTT 서비스 전체 조회
         */
        given(ottViewRepository.findAll()).willReturn(testOttViewList());

        // then
        // when
        List<OttViewDTO> findOttView = ottViewService.findAll();

        // then
        assertEquals(findOttView.size(),2);
        assertEquals(findOttView.get(0).getOttId(),1);
        assertEquals(findOttView.get(0).getOttName(),"test");
        assertEquals(findOttView.get(0).getOttImage(),"test.com");
        assertEquals(findOttView.get(1).getOttId(),2);
        assertEquals(findOttView.get(1).getOttName(),"test2");
        assertEquals(findOttView.get(1).getOttImage(),"test2.com");
    }

    @Test
    @DisplayName("Ott 서비스 : Ott 서비스 전체 조회 실패")
    void 특정_Ott_서비스_전체_조회_실패() {
        /**
         * 1. OTT 서비스 전체 조회 (예외 발생) NoSuchOttException
         */
        given(ottViewRepository.findAll()).willThrow(NoSuchOttException.class);

        // then
        assertThrows(NoSuchOttException.class, () -> ottViewService.findAll());
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
