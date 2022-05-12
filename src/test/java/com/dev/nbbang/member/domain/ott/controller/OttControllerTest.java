package com.dev.nbbang.member.domain.ott.controller;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import com.dev.nbbang.member.domain.ott.dto.request.MemberOttRequest;
import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.ott.service.MemberOttService;
import com.dev.nbbang.member.domain.ott.service.OttViewService;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OttController.class)
@ExtendWith(SpringExtension.class)
class OttControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberOttService memberOttService;

    @MockBean
    private OttViewService ottViewService;

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 등록 성공")
    void 관심_OTT_등록_성공() throws Exception {
        /**
         * URI : /ott-interest/new
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디 및 OTT ID를 통해 관심 OTT 등록하기
         */
        String uri = "/ott-interest/new";
        given(memberOttService.saveMemberOtt(anyString(), anyList())).willReturn(testMemberOttDTO());

        // when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("Test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[0].ottId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[0].ottName").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[0].ottImage").value("test2.image"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[1].ottId").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[1].ottName").value("test3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[1].ottImage").value("test3.image"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("관심 OTT 서비스 등록에 성공했습니다."))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertEquals(response.getStatus(), HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 등록 실패")
    void 관심_OTT_등록_실패() throws Exception {
        /**
         * URI : POST /ott-interest/new
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디 및 OTT ID를 통해 관심 OTT 등록하기 (예외 발생) NoCreatedMemberOttException
         */
        String uri = "/ott-interest/new";
        given(memberOttService.saveMemberOtt(anyString(), anyList())).willThrow(NoCreatedMemberOttException.class);

        // when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse();

        // then
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 조회 성공")
    void 관심_OTT_조회_성공() throws Exception {
        /**
         * URI : GET /ott-interest
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 통해 관심 OTT 모두 조회하기
         */
        String uri = "/ott-interest";
        given(memberOttService.findMemberOttByMemberId(anyString())).willReturn(testMemberOttDTO());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("Test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[0].ottId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[0].ottName").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[0].ottImage").value("test2.image"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[1].ottId").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[1].ottName").value("test3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ottView.[1].ottImage").value("test3.image"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("관심 OTT 서비스 조회에 성공했습니다."))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 조회 실패")
    void 관심_OTT_조회_실패() throws Exception {
        /**
         * URI : GET /ott-interest
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 통해 관심 OTT 모두 조회하기 (예외 발생) NoSuchMemberOttException
         */
        String uri = "/ott-interest";
        given(memberOttService.findMemberOttByMemberId(anyString())).willThrow(NoSuchMemberOttException.class);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse();

        // then
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 전체 삭제 성공")
    void 관심_OTT_전체_삭제_성공() throws Exception {
        /**
         * URI : DELETE /ott-interest
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 통해 관심 OTT 모두 삭제
         */
        String uri = "/ott-interest";
        String memberId = "Test Id";

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value());
        verify(memberOttService,times(1)).deleteAllMemberOtt(memberId);
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 전체 삭제 실패")
    void 관심_OTT_전체_삭제_실패() throws Exception {
        /**
         * URI : DELETE /ott-interest
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 통해 관심 OTT 모두 삭제 (예외 발생) NoSuchMemberOttException
         */
        String uri = "/ott-interest";
        String memberId = "Test Id";
        doThrow(NoSuchMemberOttException.class).when(memberOttService).deleteAllMemberOtt(memberId);

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 전체 한개 성공")
    void 관심_OTT_한개_삭제_성공() throws Exception {
        /**
         * URI : DELETE /ott-interest/{ottId}
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 통해 관심 OTT 모두 삭제 (예외 발생) NoSuchMemberOttException
         */
        String uri = "/ott-interest/1";
        String memberId = "Test Id";
        Integer ottId = 1;

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value());
        verify(memberOttService, times(1)).deleteMemberOtt(memberId, ottId);
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 관심 OTT 한개 삭제 실패")
    void 관심_OTT_한개_삭제_실패() throws Exception {
        /**
         * URI : DELETE /ott-interest/{ottId}
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 통해 관심 OTT 모두 삭제 (예외 발생) NoSuchMemberOttException
         */
        String uri = "/ott-interest/1";
        String memberId = "Test Id";
        Integer ottId = 1;
        doThrow(NoSuchMemberOttException.class).when(memberOttService).deleteMemberOtt(memberId, ottId);

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "Test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(status().isOk())
                .andDo(print()).andReturn().getResponse();

        // then
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 엔빵 OTT 서비스 전체 조회 성공")
    void 엔빵_OTT_서비스_전체_조회_성공() throws Exception {
        /**
         * URI : GET /ott-interest/list
         * 1. OTT 서비스 전체조회 로직 호출
         */
        String uri = "/ott-interest/list";
        List<OttViewDTO> testOttView = testOttViewDTO();
        given(ottViewService.findAll()).willReturn(testOttView);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.ottView.[0].ottId").value(1))
                .andExpect(jsonPath("$.data.ottView.[0].ottName").value("test"))
                .andExpect(jsonPath("$.data.ottView.[0].ottImage").value("test.image"))
                .andExpect(jsonPath("$.data.ottView.[1].ottId").value(2))
                .andExpect(jsonPath("$.data.ottView.[1].ottName").value("test2"))
                .andExpect(jsonPath("$.data.ottView.[1].ottImage").value("test2.image"))
                .andExpect(jsonPath("$.data.ottView.[2].ottId").value(3))
                .andExpect(jsonPath("$.data.ottView.[2].ottName").value("test3"))
                .andExpect(jsonPath("$.data.ottView.[2].ottImage").value("test3.image"))
                .andExpect(jsonPath("$.message").value("등록된 모든 OTT 서비스 상세정보 조회에 성공했습니다."))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    @DisplayName("OTT 컨트롤러 : 엔빵 OTT 서비스 전체 조회 실패")
    void 엔빵_OTT_서비스_전체_조회_실패() throws Exception {
        /**
         * URI : GET /ott-interest/list
         * 1. OTT 서비스 전체조회 로직 호출
         */
        String uri = "/ott-interest/list";
        given(ottViewService.findAll()).willThrow(NoSuchOttException.class);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

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
        Member testMember = Member.builder()
                .memberId("Test Id")
                .nickname("Test Nickname")
                .point(0L)
                .exp(0L)
                .grade(Grade.BRONZE)
                .partyInviteYn("Y")
                .build();
        memberOtt.add(MemberOtt.builder().member(testMember).ottView(testOttView().get(0)).build());
        memberOtt.add(MemberOtt.builder().member(testMember).ottView(testOttView().get(1)).build());

        return memberOtt;
    }

    private static List<OttView> testOttView() {
        List<OttView> ottView = new ArrayList<>();
        ottView.add(new OttView(2, "test2", "test2.image"));
        ottView.add(new OttView(3, "test3", "test3.image"));

        return ottView;
    }

    private static List<OttViewDTO> testOttViewDTO() {
        List<OttViewDTO> ottView = new ArrayList<>();
        ottView.add(new OttViewDTO(1, "test", "test.image"));
        ottView.add(new OttViewDTO(2, "test2", "test2.image"));
        ottView.add(new OttViewDTO(3, "test3", "test3.image"));

        return ottView;
    }


    private static List<MemberOttDTO> testMemberOttDTO() {
        List<MemberOttDTO> memberOtt = new ArrayList<>();
        memberOtt.add(MemberOttDTO.builder().member(testMember()).ottView(testOttView().get(0)).build());
        memberOtt.add(MemberOttDTO.builder().member(testMember()).ottView(testOttView().get(1)).build());

        return memberOtt;
    }

    private static MemberOttRequest testRequest() {
        String memberId = "Test Id";
        List<Integer> ottId = new ArrayList<>(Arrays.asList(2, 3));

        return MemberOttRequest.builder()
                .memberId(memberId)
                .ottId(ottId).build();
    }
}