package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.user.api.SocialLoginType;
import com.dev.nbbang.member.domain.user.dto.request.MemberReq;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.domain.user.util.KakaoAuthUrl;
import com.dev.nbbang.member.global.util.JwtUtil;
import com.dev.nbbang.member.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final KakaoAuthUrl kakaoAuthUrl;

    @GetMapping(value = "/{socialLoginType}")
    public void socialLoginType(@PathVariable(name = "socialLoginType")SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);
        memberService.request(socialLoginType);
    }

    @GetMapping(value = "/{socialLoginType}/callback")
    public Object callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                           @RequestParam(name="code") String code, HttpServletResponse res) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);
        Map<String, Object> result = new HashMap<>();
        Map<String, String> userInfo = memberService.socialLogin(socialLoginType, code);
        if(userInfo == null) {
            log.info("badRequest");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Member member = memberService.findByMember_Id("G_" + userInfo.get("id"));
        if(member == null) {
            log.info("회원가입필요");
            result.put("member_id", "G_" + userInfo.get("id"));
            result.put("nickname", userInfo.get("name"));
            result.put("isSignUp", false);
            return new ResponseEntity(result, HttpStatus.OK);
        }

        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());

        res.setHeader("Authorization", "Bearer " + accessToken);
        redisUtil.setData(member.getMemberId(), refreshToken,  JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
        result.put("member_id", member.getMemberId());
        result.put("nickname", member.getNickname());
        result.put("grade", member.getGrade());
        result.put("point", member.getPoint());

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public Object signUp(@RequestBody MemberReq memberReq, HttpServletResponse res) {
        Map<String, Object> result = new HashMap<>();
        List<OTTView> ottViewList = new ArrayList<>();
        for(int ottId : memberReq.getOttId()) {
            ottViewList.add(memberService.findByOttId(ottId));
        }
        Member member = memberService.memberSave(Member.builder().memberId(memberReq.getMemberId()).nickname(memberReq.getNickname()).
                bankId(1).grade("이등병").point(0).exp(0).billingKey(memberReq.getBillingKey()).partyInviteYn('Y').build());

        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());

        res.setHeader("Authorization", "Bearer " + accessToken);
        redisUtil.setData(member.getMemberId(), refreshToken,  JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
        result.put("member_id", member.getMemberId());
        result.put("nickname", member.getNickname());
        result.put("grade", member.getGrade());
        result.put("point", member.getPoint());

        return new ResponseEntity(result, HttpStatus.OK);
    }

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
    public String kakaoSocialLogin(@PathVariable com.dev.nbbang.member.domain.user.entity.SocialLoginType socialLoginType,
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
