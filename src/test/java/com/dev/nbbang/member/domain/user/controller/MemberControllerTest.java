package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.service.OttViewService;
import com.dev.nbbang.member.domain.user.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.member.domain.user.api.util.KakaoAuthUrl;
import com.dev.nbbang.member.domain.user.api.util.SocialAuthUrl;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.dto.request.MemberExpRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberGradeRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberModifyRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberRegisterRequest;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.FailLogoutMemberException;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.
        *;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SecurityConfig.class)})
@ExtendWith(SpringExtension.class)
@WithMockUser
class MemberControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private OttViewService ottViewService;
    @MockBean
    private SocialTypeMatcher socialTypeMatcher;

    @MockBean
    private SocialAuthUrl socialAuthUrl;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 컨트롤러 : 소셜 로그인 인가코드 URL 생성 성공")
    void 소셜_로그인_인가코드_URL_생성_성공() throws Exception {
        //given
        String uri = "/members/oauth/kakao";
        given(socialTypeMatcher.findSocialAuthUrlByType(any())).willReturn(new KakaoAuthUrl());
        given(socialAuthUrl.makeAuthorizationUrl()).willReturn("https://kauth.kakao.com/oauth/authorize?client_id&redirect_uri&response_type=code");

        //when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authUrl").value("https://kauth.kakao.com/oauth/authorize?client_id&redirect_uri&response_type=code"))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 소셜 로그인 인가코드 URL 생성 실패")
    void 소셜_로그인_인가코드_생성_실패() throws Exception {
        //given
        String uri = "/members/oauth/kakao";
        given(socialTypeMatcher.findSocialAuthUrlByType(any())).willThrow(IllegalSocialTypeException.class);

        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 소셜 로그인 실패")
    void 소셜_로그인_실패() throws Exception {
        // given
        String uri = "/members/oauth/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn(null);

        // when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .param("code", "testCode"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse();

        
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 소셜 로그인 성공")
    void 소셜_로그인_성공() throws Exception {
        //given
        String uri = "/members/oauth/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willReturn(testMemberDTO());
        given(memberService.manageToken(any())).willReturn("testToken");

        //when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .param("code", "testCode"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value("BRONZE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exp").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(0))
                .andDo(print())
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer testToken");
    }

    @Test
    @DisplayName("회원 컨트롤러 : 소셜 로그인 회원 없는 경우 성공")
    void 소셜_로그인_회원_없는_경우_성공() throws Exception {
        // given
        String uri = "/members/oauth/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willThrow(NoSuchMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .param("code", "testCode"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.registerYn").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 추가 회원 가입 실패")
    void 추가_회원_가입_실패() throws Exception {
        // given
        String uri = "/members/new";
//        given(memberService.findByOttId(anyInt())).willReturn(new OttView(1, "test", "test.image"));
        given(memberService.saveMember(any(), anyList())).willThrow(NoCreateMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                post(uri).with(csrf())
                        .content(objectMapper.writeValueAsString(testRegisterMember()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원정보 저장에 실패했습니다."))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 추가 회원 가입 성공")
    void 추가_회원_가입_성공() throws Exception {
        // given
        String uri = "/members/new";
//        given(memberService.findByOttId(anyInt())).willReturn(new OttView(1, "test", "test.image"));
//        given(memberService.saveMember(any())).willReturn(testMemberDTO());
        given(memberService.saveMember(any(), anyList())).willReturn(testMemberDTO());
        given(memberService.manageToken(any())).willReturn("new Token");


        //when
        MockHttpServletResponse response = mvc.perform(
                post(uri).with(csrf())
                        .content(objectMapper.writeValueAsString(testRegisterMember()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value("BRONZE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exp").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(0))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer new Token");
    }

    @Test
    @DisplayName("회원 컨트롤러 : 닉네임으로 추천인 조회하기 성공")
    void 닉네임으로_추천인_조회하기_성공() throws Exception {
        // given
        String uri = "/members/recommend/닉네임";
        given(memberService.findMemberByNickname(anyString())).willReturn(testMemberDTO());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("testNickname"))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 닉네임으로 추천인 조회하기 실패")
    void 닉네임으로_추천인_조회하기_실패() throws Exception {
        // given
        String uri = "/members/recommend/닉네임";
        given(memberService.findMemberByNickname(anyString())).willThrow(NoSuchMemberException.class);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 닉네임 중복 확인 성공")
    void 닉네임_중복확인_성공() throws Exception {
        // given
        String uri = "/members/nickname/닉네임";
        given(memberService.duplicateNickname(anyString())).willReturn(true);

        //when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 닉네임 중복 확인 실패")
    void 닉네임_중복확인_실패() throws Exception {
        // given
        String uri = "/members/nickname/닉네임";
        given(memberService.duplicateNickname(anyString())).willThrow(NoSuchMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("회원 컨트롤러 : 닉네임 리스트 가져오기 성공")
    void 닉네임_리스트_가져오기_성공() throws Exception {
        // given
        String uri = "/members/nickname/list/닉네임";
        given(memberService.findMemberListByNickname(anyString())).willReturn(testMemberDTOlist());

        //when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].nickname").value("testNickname"))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 닉네임 리스트 가져오기 실패")
    void 닉네임_리스트_가져오기_실패() throws Exception {
        // given
        String uri = "/members/nickname/list/닉네임";
        given(memberService.findMemberListByNickname(anyString())).willThrow(NoSuchMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 등급 조회 성공")
    void 회원_등급_조회_성공() throws Exception {
        // given
        String uri = "/members/grade";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willReturn(testMemberDTO());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value("BRONZE"))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러: 회원 등급 조회 실패")
    void 회원_등급_조회_실패() throws Exception {
        // given
        String uri = "/members/grade";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willThrow(NoSuchMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 등급 수정 성공")
    void 회원_등급_수정_성공() throws Exception {
        //given
        String uri = "/members/grade";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.updateGrade(anyString(), any())).willReturn(updatedMemberDTO());

        //when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testMemberGradeRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value("DIAMOND"))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 등급 수정 실패")
    void 회원_등급_수정_실패() throws Exception {
        //given
        String uri = "/members/grade";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.updateGrade(anyString(), any())).willThrow(NoCreateMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testMemberGradeRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 경험치 변동 성공")
    void 회원_경험치_변동_성공() throws Exception {
        //given
        String uri = "/members/exp";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.updateExp(anyString(), any())).willReturn(updatedMemberDTO());

        //when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testMemberExpRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exp").value(100))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 경험치 변동 실패")
    void 회원_경험치_변동_실패() throws Exception {
        //given
        String uri = "/members/exp";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.updateExp(anyString(), any())).willThrow(NoCreateMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testMemberExpRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 정보 불러오기 성공")
    void 회원_정보_불러오기_성공() throws Exception {
        //given
        String uri = "/members/profile";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willReturn(testMemberDTO());

        //when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[0].ottId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[0].ottName").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[0].ottImage").value("test.image"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value("BRONZE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exp").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.partyInviteYn").value("Y"))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 정보 불러오기 실패")
    void 회원_정보_불러오기_실패() throws Exception {
        // given
        String uri = "/members/profile";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willThrow(NoSuchMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 정보 수정하기 성공")
    void 회원_정보_수정하기_성공() throws Exception {
        // given
        String uri = "/members/profile";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willReturn(testMemberDTO());
        given(memberService.updateMember(anyString(), any(), anyList())).willReturn(updatedMemberDTO());
        given(memberService.manageToken(any())).willReturn("update token");

        //when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testMemberModifyRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("updateNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[0].ottId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[0].ottName").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[0].ottImage").value("test2.image"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[1].ottId").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[1].ottName").value("test3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ottView.[1].ottImage").value("test3.image"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.partyInviteYn").value("N"))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer update token");
    }

    @Test
    @DisplayName("회원 컨트롤러 : 회원 정보 수정하기 실패")
    void 회원_정보_수정하기_실패() throws Exception {
        //given
        String uri = "/members/profile";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.updateMember(anyString(), any(), anyList())).willThrow(NoCreateMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(put(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken")
                .content(objectMapper.writeValueAsString(testMemberModifyRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 로그아웃 성공")
    void 로그아웃_성공() throws Exception {
        //given
        String uri = "/members/logout";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.logout(anyString())).willReturn(true);

        //when
        //when
        MockHttpServletResponse response = mvc.perform(delete(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("회원 컨트롤러 : 로그아웃 실패")
    void 로그아웃_실패() throws Exception {
        //given
        String uri = "/members/logout";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.logout(anyString())).willThrow(FailLogoutMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(delete(uri).with(csrf())
                .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private static List<MemberOtt> testMemberOtt() {
        List<MemberOtt> memberOtt = new ArrayList<>();
        Member testMember = Member.builder().memberId("testId")
                .nickname("testNickname")
                .grade(Grade.BRONZE)
                .exp(0L)
                .point(0L)
                .partyInviteYn("Y")
                .build();

        memberOtt.add(MemberOtt.builder().member(testMember).ottView(new OttView(1, "test", "test.image")).build());

        return memberOtt;
    }

    private static MemberDTO testMemberDTO() {
        return MemberDTO.builder().memberId("testId")
                .nickname("testNickname")
                .memberOtt(testMemberOtt())
                .grade(Grade.BRONZE)
                .exp(0L)
                .point(0L)
                .partyInviteYn("Y")
                .build();
    }

    private static MemberDTO updatedMemberDTO() {
        return MemberDTO.builder()
                .memberId("testId")
                .nickname("updateNickname")
                .memberOtt(updatedMemberOtt())
                .grade(Grade.DIAMOND)
                .exp(100L)
                .point(10000L)
                .partyInviteYn("N")
                .build();
    }

    private static List<MemberOtt> updatedMemberOtt() {
        List<MemberOtt> memberOtt = new ArrayList<>();
        Member updatedMember = Member.builder().memberId("testId")
                .nickname("updatedNickname")
                .grade(Grade.DIAMOND)
                .exp(1000L)
                .point(10000L)
                .partyInviteYn("N")
                .build();

        memberOtt.add(MemberOtt.builder().member(updatedMember).ottView(new OttView(2, "test2", "test2.image")).build());
        memberOtt.add(MemberOtt.builder().member(updatedMember).ottView(new OttView(3, "test3", "test3.image")).build());

        return memberOtt;
    }

    private static List<MemberDTO> testMemberDTOlist() {
        List<MemberDTO> list = new ArrayList<>();
        list.add(testMemberDTO());

        return list;
    }

    private static MemberRegisterRequest testRegisterMember() {
        List<Integer> ottId = new ArrayList<>();
        ottId.add(1);
        return MemberRegisterRequest.builder().memberId("testId")
                .nickname("testNickname")
                .ottId(ottId)
                .build();
    }

    private static MemberGradeRequest testMemberGradeRequest() {
        return MemberGradeRequest.builder()
                .memberId("testId")
                .grade(Grade.DIAMOND).build();
    }

    private static MemberExpRequest testMemberExpRequest() {
        return MemberExpRequest.builder()
                .memberId("testId")
                .exp(100L).build();
    }

    private static MemberModifyRequest testMemberModifyRequest() {
        List<Integer> ottId = new ArrayList<>();
        ottId.add(2);
        ottId.add(3);
        return MemberModifyRequest.builder()
                .memberId("testId")
                .nickname("updateNickname")
                .ottId(ottId)
                .partyInviteYn("N")
                .build();
    }
}