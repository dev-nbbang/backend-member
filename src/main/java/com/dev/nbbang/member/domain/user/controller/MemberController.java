package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.user.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.domain.user.util.KakaoAuthUrl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
public class MemberController {

    private final MemberService memberService;
    private final KakaoAuthUrl kakaoAuthUrl;

    @GetMapping(value="/kakao/authorization")
    @Operation(description = "카카오 소셜 로그인 인가 코드 요청 URL을 생성한다.")
    public ResponseEntity<?> makeKakaoAuthUrl() {
        Map<String, Object> resultMap = new HashMap<>();

        // 카카오 인가 코드 URL 만들어 Return
        try {
            resultMap.put("auth-url", kakaoAuthUrl.makeAuthorizationUrl());
            resultMap.put("status", true);
            resultMap.put("message", "카카오 인가 코드 요청 URL 생성 완료");
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    @GetMapping(value = "/kakao/test")
    @Operation(description = "백엔드 소셜 로그인 인가 코드 요청 테스트")
    public void testAuthUrl(HttpServletResponse httpServletResponse) throws IOException {
        String authUrl = kakaoAuthUrl.makeAuthorizationUrl();

        System.out.println("authUrl = " + authUrl);
        httpServletResponse.sendRedirect(authUrl);
    }


    @GetMapping(value = "/{socialLoginType}/callback")
    @Operation(description = "카카오 소셜 로그인 인가 코드 요청 성공 시 인가 코드를 전달해 엑세스 토큰 발급을 요청한다.")
    public String kakaoSocialLogin(@PathVariable SocialLoginType socialLoginType,
                                   @RequestParam(name = "code") String code) {
        try {
            memberService.kakaoLogin(socialLoginType, code);
            System.out.println("test");

        }
        catch(Exception e) {
            return "false";
        }
        return null;
    }


}
