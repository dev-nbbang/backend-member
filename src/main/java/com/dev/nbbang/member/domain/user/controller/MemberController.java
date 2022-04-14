package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.util.SocialAuthUrl;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.request.MemberRequest;
import com.dev.nbbang.member.domain.user.dto.response.MemberResponse;
import com.dev.nbbang.member.domain.user.dto.response.NicknameMemberResponse;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.service.MemberService;
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
import java.util.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final SocialTypeMatcher socialTypeMatcher;


    @GetMapping(value = "/{socialLoginType}")
    @Operation(description = "소셜 로그인 인가코드 URL을 생성한다.")
    public Object socialLoginType(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);

        // PathVariable의 소셜 타입을 인가 코드 URL 생성 (카카오, 구글)
        SocialAuthUrl socialAuthUrl = socialTypeMatcher.findSocialAuthUrlByType(socialLoginType);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("auth-url", socialAuthUrl.makeAuthorizationUrl());
        resultMap.put("status", true);
        resultMap.put("message", "URL 생성 완료");
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @GetMapping(value = "/{socialLoginType}/callback")
    @Operation(description = "동의 정보 인증 후 리다이렉트 URI")
    public ResponseEntity<?> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                      @RequestParam(name = "code") String code, HttpServletResponse res) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);
        Map<String, Object> result = new HashMap<>();
        // 소셜 로그인 실패시
        String memberId = memberService.socialLogin(socialLoginType, code);
        if (memberId == null) {
            log.info("badRequest");
            // Message 넘기기
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            MemberResponse member = memberService.findMember(memberId);

            String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
            String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());

            res.setHeader("Authorization", "Bearer " + accessToken);
            redisUtil.setData(member.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

            result.put("memberId", member.getMemberId());
            result.put("nickname", member.getNickname());
            result.put("grade", member.getGrade());
            result.put("point", member.getPoint());

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchMemberException e){
            log.info(e.getMessage());
            log.info("회원가입필요");
            result.put("memberId", memberId);
            result.put("isSignUp", false);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/new")
    @Operation(description = "추가 회원 가입")
    public Object signUp(@RequestBody MemberRequest memberRequest, HttpServletResponse res) {
        Map<String, Object> result = new HashMap<>();
        List<OTTView> ottViewList = new ArrayList<>();
        // 관심 OTT 저장하기 (Ott 없는 경우 있음)
        for (int ottId : memberRequest.getOttId()) {
            ottViewList.add(memberService.findByOttId(ottId));
        }

        // 요청 데이터 엔티티에 저장
        Member member = memberService.memberSave(
                Member.builder().memberId(memberRequest.getMemberId())
                        .nickname(memberRequest.getNickname())
                        .ottView(ottViewList)
                        .build());

        // 회원 생성이 완료된 경우
        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());

        res.setHeader("Authorization", "Bearer " + accessToken);
        redisUtil.setData(member.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
        result.put("memberId", member.getMemberId());
        result.put("nickname", member.getNickname());
        result.put("grade", member.getGrade());
        result.put("point", member.getPoint());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/{socialLoginType}/test")
    @Operation(description = "백엔드 소셜 로그인 인가 코드 요청 테스트")
    public void test(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType, HttpServletResponse httpServletResponse) throws IOException {
        SocialAuthUrl socialAuthUrl = socialTypeMatcher.findSocialAuthUrlByType(socialLoginType);
        String authUrl = socialAuthUrl.makeAuthorizationUrl();

        System.out.println("authUrl = " + authUrl);
        httpServletResponse.sendRedirect(authUrl);
    }

    @GetMapping(value = "/{nickname}/recommend")
    @Operation(description = "닉네임으로 추천인 회원 조회하기")
    public ResponseEntity<Map<String,Object>> findRecommendMember(@PathVariable(name = "nickname") String nickname) {
        log.info(">> [Nbbang Member Service] 닉네임으로 추천인 회원 조회하기");
        Map<String, Object> result = new HashMap<>();
        try {
            NicknameMemberResponse member = memberService.findMemberByNickname(nickname);
            result.put("memberId", member.getMemberId());
            result.put("nickname", member.getNickname());
            result.put("status", true);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(NoSuchMemberException e) {
            log.info(e.getMessage());
            result.put("message", e.getMessage());
            result.put("status", false);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }
}
