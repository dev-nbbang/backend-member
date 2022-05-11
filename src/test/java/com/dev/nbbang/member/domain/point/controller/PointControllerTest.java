package com.dev.nbbang.member.domain.point.controller;

import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.dto.request.MemberPointRequest;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.point.exception.NoCreatedPointDetailsException;
import com.dev.nbbang.member.domain.point.service.PointService;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.config.SecurityConfig;
import com.dev.nbbang.member.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SecurityConfig.class)})
@ExtendWith(SpringExtension.class)
@WithMockUser
class PointControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PointService pointService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("포인트 컨트롤러 : 포인트 조회 성공")
    void 포인트_조회_성공() throws Exception {
        /**
         * URI : GET /point
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 이용해 현재 회원 포인트
         */
        // given
        String uri = "/point";
        given(jwtUtil.getUserid(anyString())).willReturn("test Id");
        given(memberService.findMember(anyString())).willReturn(testMember());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.point").value(150))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원의 현재 포인트 조회에 성공했습니다."))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("포인트 컨트롤러 : 포인트 조회 실패")
    void 포인트_조회_실패() throws Exception {
        /**
         * URI : GET /point
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 이용해 현재 회원 포인트 (예외발생) -> NoSuchMemberException
         */
        // given
        String uri = "/point";
        given(jwtUtil.getUserid(anyString())).willReturn("test Id");
        given(memberService.findMember(anyString())).willThrow(NoSuchMemberException.class);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("포인트 컨트롤러 : 포인트 적립/시용 성공")
    void 포인트_적립_사용_성공() throws Exception {
        /**
         * URI : POST /point
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 포인트를 적립 및 사용한다.
         */
        // given
        String uri = "/point";
        given(jwtUtil.getUserid(anyString())).willReturn("test Id");
        given(pointService.updatePoint(anyString(), any())).willReturn(testIncreasePoint());

        //when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.usePoint").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointType").value("INCREASE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointDetail").value("포인트 적립 테스트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("포인트 적립/사용에 성공했습니다."))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

    }

    @Test
    @DisplayName("포인트 컨트롤러 : 포인트 적립/사용 실패")
    void 포인트_적립_사용_실패() throws Exception {
        /**
         * URI : POST /point
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 포인트를 적립 및 사용한다. (예외발생) -> NoCreatedPointDetailException
         */
        // given
        String uri = "/point";
        given(jwtUtil.getUserid(anyString())).willReturn("test Id");
        given(pointService.updatePoint(anyString(), any())).willThrow(NoCreatedPointDetailsException.class);

        // when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("포인트 컨트롤러 : 포인트 상세이력 조회 성공")
    void 포인트_상세이력_조회_성공() throws Exception {
        /**
         * URI : GET /point/details
         * 1. JWT 토큰 회원 아이디 파싱
         * 2. 회원 아이디를 이용해 회원의 포인트 상세이력을 조회한다.
         */
        // given
        String uri = "/point/details";
        given(jwtUtil.getUserid(anyString())).willReturn("test Id");
        given(pointService.findPointDetails(anyString(), anyLong(), anyInt())).willReturn(testPointDetails());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken")
                .param("pointId","2")
                .param("size","3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("test Id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointDetails.[0].pointType").value("INCREASE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointDetails.[0].usePoint").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointDetails.[0].pointDetail").value("포인트 적립 테스트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointDetails.[1].pointType").value("DECREASE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointDetails.[1].usePoint").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.pointDetails.[1].pointDetail").value("포인트 사용 테스트"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원의 포인트 상세이력 조회에 성공했습니다."))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("포인트 컨트롤러 : 포인트 상세이력 조회 실패")
    void 포인트_상세이력_조회_실패() throws Exception {
        /**
         * URI : GET /point/details?pointId=1&size=3
         * 1. JWT 토큰 회원 아이디 파싱 (예외 발생) -> NoSuchMemberException
         * 2. 회원 아이디를 이용해 회원의 포인트 상세이력을 조회한다.
         */
        // given
        String uri = "/point/details";
        given(jwtUtil.getUserid(anyString())).willReturn("test Id");
        given(pointService.findPointDetails(anyString(), anyLong(), anyInt())).willThrow(NoSuchMemberException.class);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken")
                .param("pointId","2")
                .param("size","3"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private static MemberDTO testMember() {
        return MemberDTO.builder()
                .memberId("test Id")
                .nickname("test Nickname")
                .point(150L)
                .build();
    }

    private static MemberPointRequest testRequest() {
        return MemberPointRequest.builder()
                .memberId("test Id")
                .pointType(PointType.INCREASE)
                .usePoint(500L)
                .pointDetail("포인트 적립 테스트")
                .build();
    }

    private static PointDTO testIncreasePoint() {
        return PointDTO.builder()
                .member(Member.builder().memberId("test Id").point(650L).build())
                .pointType(PointType.INCREASE)
                .usePoint(500L)
                .pointDetail("포인트 적립 테스트")
                .build();
    }

    private static PointDTO testDecreasePoint() {
        return PointDTO.builder()
                .member(Member.builder().memberId("test Id").point(650L).build())
                .pointType(PointType.DECREASE)
                .usePoint(500L)
                .pointDetail("포인트 사용 테스트")
                .build();
    }

    private static List<PointDTO> testPointDetails() {
        return new ArrayList<>(Arrays.asList(testIncreasePoint(), testDecreasePoint()));
    }
}