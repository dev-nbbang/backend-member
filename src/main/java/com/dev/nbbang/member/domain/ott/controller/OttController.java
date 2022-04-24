package com.dev.nbbang.member.domain.ott.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/ott-interest")
public class OttController {

    @PostMapping(value = "/new")
    @Operation(summary = "관심 OTT 등록", description = "회원 아이디와 OTT ID를 받아 관심 OTT로 등록한다.")
    public ResponseEntity<?> registerMemberOtt(HttpServletResponse servletResponse) {

        return null;
    }

    @GetMapping
    @Operation(summary = "관심 OTT 조회", description = "회원 아이디를 이용해 관심 OTT를 조회한다.")
    public ResponseEntity<?> searchMemberOtt(HttpServletResponse servletResponse) {

        return null;
    }

    @DeleteMapping(value = "/{ottId}")
    @Operation(summary = "관심 OTT 삭제", description = "회원 아이디와 OTT Id를 받아 등록된 관심 OTT를 삭제한다")
    public ResponseEntity<?> releaseMemberOtt(@PathVariable(name = "ottId") Integer ottId,  HttpServletResponse servletResponse) {

        return null;
    }

    @DeleteMapping
    @Operation(summary = "관심 OTT 전체 삭제", description = "회원 아이디를 받아 등록된 관심 OTT를 전체 삭제한다")
    public ResponseEntity<?> releaseAllMemberOtt(HttpServletResponse servletResponse) {

        return null;
    }
}

