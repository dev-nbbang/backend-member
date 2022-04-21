package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.dto.request.MemberRegisterRequest;
import com.dev.nbbang.member.domain.user.dto.response.MemberNicknameResponse;
import com.dev.nbbang.member.domain.user.dto.response.MemberRegisterResponse;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.config.SecurityConfig;
import com.dev.nbbang.member.global.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SecurityConfig.class)})
@ExtendWith(SpringExtension.class)
@WithMockUser
class MemberControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private SocialTypeMatcher socialTypeMatcher;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 컨트롤러 : 소셜 로그인 실패")
    void 소셜_로그인_실패() throws Exception {
        // given
        String uri = "/members/oauth/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn(null);
        given(memberService.manageToken(any())).willReturn("testToken");

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
        given(memberService.findByOttId(anyInt())).willReturn(new OTTView(1, "test", "test.image"));
        given(memberService.saveMember(any())).willThrow(NoCreateMemberException.class);

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
        given(memberService.findByOttId(anyInt())).willReturn(new OTTView(1, "test", "test.image"));
        given(memberService.saveMember(any())).willReturn(testMemberDTO());

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
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("Authorization","Bearer testAccessToken");
        String uri = "/members/grade";
        given(jwtUtil.getUserid(anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willReturn(testMemberDTO());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value("BRONZE"))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private static List<OTTView> testOttView() {
        List<OTTView> ottView = new ArrayList<>();
        ottView.add(new OTTView(1, "test", "test.image"));
        return ottView;
    }

    private static MemberDTO testMemberDTO() {

        return MemberDTO.builder().memberId("testId")
                .nickname("testNickname")
                .ottView(testOttView())
                .grade(Grade.BRONZE)
                .exp(0L)
                .point(0L)
                .partyInviteYn("Y")
                .build();
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

}