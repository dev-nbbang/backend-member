package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@ExtendWith(SpringExtension.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 컨트롤러 : 소셜 로그인 성공")
    void 소셜_로그인_성공() throws Exception {
        //given
        String uri = "/member/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willReturn(testMemberDTO());


        //when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .param(SocialLoginType.KAKAO.name(), "testCode"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }
    private static MemberDTO testMemberDTO() {
        return MemberDTO.builder().memberId("testId")
                .nickname("testNickname")
                .grade(Grade.BRONZE)
                .exp(0L)
                .point(0L)
                .partyInviteYn("Y")
                .build();
    }
}