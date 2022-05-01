package com.dev.nbbang.member.domain.point.controller;

import com.dev.nbbang.member.domain.point.service.PointService;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
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
}