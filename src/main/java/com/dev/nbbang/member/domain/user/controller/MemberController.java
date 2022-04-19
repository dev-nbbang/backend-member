package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.util.SocialAuthUrl;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.dto.request.MemberExpRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberGradeRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberModifyRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberRegisterRequest;
import com.dev.nbbang.member.domain.user.dto.response.*;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.domain.user.exception.FailLogoutMemberException;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.util.JwtUtil;
import com.dev.nbbang.member.global.util.RedisUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/members")
@Slf4j
@Tag(name = "Member", description = "Member API")
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final SocialTypeMatcher socialTypeMatcher;


    @GetMapping(value = "/oauth/{socialLoginType}")
    @Operation(summary = "소셜 로그인 인가코드 URL", description = "소셜 로그인 인가코드 URL을 생성한다.")
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

    @GetMapping(value = "/oauth/{socialLoginType}/callback")
    @Operation(summary = "동의 정보 인증 후 리다이렉트", description = "동의 정보 인증 후 리다이렉트 URI")
    public ResponseEntity<?> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                      @RequestParam(name = "code") String code, HttpServletResponse res) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);

//        Map<String, Object> result = new HashMap<>();

        // 소셜 로그인 실패시
        String memberId = memberService.socialLogin(socialLoginType, code);
        if (memberId == null) {
            log.info("badRequest");
            // Message 넘기기
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            MemberDTO findMember = memberService.findMember(memberId);

            // 회원 닉네임 수정 시 JWT 새로 생성 및 레디스 값 갱신
            String accessToken = jwtUtil.generateAccessToken(findMember.getMemberId(), findMember.getNickname());
            String refreshToken = jwtUtil.generateRefreshToken(findMember.getMemberId(), findMember.getNickname());
            res.setHeader("Authorization", "Bearer " + accessToken);

            // redis에 refresh Token 저장
            redisUtil.setData(findMember.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

            // 회원 정보
//            result.put("memberId", member.getMemberId());
//            result.put("nickname", member.getNickname());
//            result.put("grade", member.getGrade());
//            result.put("point", member.getPoint());

//            return new ResponseEntity<>(result, HttpStatus.OK);
            return new ResponseEntity<>(MemberDefaultInfoResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
            log.info("회원가입필요");
//            result.put("memberId", memberId);
//            result.put("isSignUp", false);
            return new ResponseEntity<>(MemberRegisterResponse.create(memberId, false, true), HttpStatus.OK);
        }
    }

    @PostMapping("/new")
    @Operation(summary = "추가 회원 가입", description = "추가 회원 가입")
    public ResponseEntity<?> signUp(@RequestBody MemberRegisterRequest request, HttpServletResponse servletResponse) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<OTTView> ottViewList = new ArrayList<>();
            // 관심 OTT 저장하기 (Ott 없는 경우 있음)
            for (int ottId : request.getOttId()) {
                ottViewList.add(memberService.findByOttId(ottId));
            }

            // 요청 데이터 엔티티에 저장
            MemberDTO member = memberService.memberSave(
                    Member.builder().memberId(request.getMemberId())
                            .nickname(request.getNickname())
                            .ottView(ottViewList)
                            .build());


            // 회원 생성이 완료된 경우
            String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
            String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());

            servletResponse.setHeader("Authorization", "Bearer " + accessToken);
            redisUtil.setData(member.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
            log.info("redis 저장 완료");


//            result.put("memberId", member.getMemberId());
//            result.put("nickname", member.getNickname());
//            result.put("grade", member.getGrade());
//            result.put("point", member.getPoint());

//            return new ResponseEntity<>(result, HttpStatus.CREATED);
            return new ResponseEntity<>(MemberDefaultInfoResponse.create(member, true), HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {
            result.put("status", false);
            result.put("message", e.getMessage());

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/oauth/{socialLoginType}/test")
    @Operation(summary = "백엔드 소셜 로그인 인가 코드 요청", description = "백엔드 소셜 로그인 인가 코드 요청 테스트")
    public void test(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType, HttpServletResponse httpServletResponse) throws IOException {
        SocialAuthUrl socialAuthUrl = socialTypeMatcher.findSocialAuthUrlByType(socialLoginType);
        String authUrl = socialAuthUrl.makeAuthorizationUrl();

        System.out.println("authUrl = " + authUrl);
        httpServletResponse.sendRedirect(authUrl);
    }

    @GetMapping(value = "/recommend/{nickname}")
    @Operation(summary = "닉네임으로 추천인 회원 조회하기", description = "닉네임으로 추천인 회원 조회하기")
    public ResponseEntity<?> findRecommendMember(@PathVariable(name = "nickname") String nickname) {
        log.info(">> [Nbbang Member Service] 닉네임으로 추천인 회원 조회하기");
        Map<String, Object> result = new HashMap<>();
        try {
            MemberDTO findMember = memberService.findMemberByNickname(nickname);
//            result.put("memberId", member.getMemberId());
//            result.put("nickname", member.getNickname());
//            result.put("status", true);

            return new ResponseEntity<>(MemberNicknameResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
            result.put("message", e.getMessage());
            result.put("status", false);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/nickname/{nickname}")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인")
    public ResponseEntity<?> checkDuplicateNickname(@PathVariable(name = "nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 중복 확인");
        Map<String, Object> result = new HashMap<>();
        try {
            boolean nicknameDup = memberService.duplicateNickname(nickname);
            result.put("nicknameDup", nicknameDup);
            result.put("status", true);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
            result.put("message", e.getMessage());
            result.put("status", false);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/nickname/list/{nickname}")
    @Operation(summary = "닉네임 리스트 가져오기", description = "닉네임 리스트 가져오기")
    public ResponseEntity<?> searchNicknameList(@PathVariable(name = "nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 리스트 가져오기");
        Map<String, Object> result = new HashMap<>();

        try {
            List<MemberDTO> findMemberList = memberService.findMemberListByNickname(nickname);
            //DTO 넣어서 보내버리기

            return new ResponseEntity<>(MemberNicknameResponse.createList(findMemberList, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/grade")
    @Operation(description = "회원 등급 조회")
    public ResponseEntity<?> getMemberGrade(HttpServletRequest servletRequest) {
        log.info(" >>  [Nbbang Member Service] 회원 등급 조회");
        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            MemberDTO findMember = memberService.findMember(memberId);
//            result.put("memberId", findMember.getMemberId());
//            result.put("grade", findMember.getGrade());
//            result.put("status", true);
//
//            return new ResponseEntity<>(result, HttpStatus.OK);
            return new ResponseEntity<>(MemberGradeResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
            result.put("message", e.getMessage());
            result.put("status", false);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @PutMapping(value = "/grade")
    @Operation(description = "회원 등급 수정")
    public ResponseEntity<?> modifyMemberGrade(@RequestBody MemberGradeRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 등급 수정");
        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            MemberDTO findMember = memberService.findMember(memberId);
            MemberDTO updatedMember = memberService.memberSave(Member.builder()
                    .memberId(findMember.getMemberId())
                    .grade(request.getGrade()).build());

//            result.put("memberId", updatedMember.getMemberId());
//            result.put("grade", updatedMember.getGrade());
//            result.put("status", true);
//
//            return new ResponseEntity<>(result, HttpStatus.CREATED);
            return new ResponseEntity<>(MemberGradeResponse.create(updatedMember, true), HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {
            log.info(e.getMessage());
            result.put("message", e.getMessage());
            result.put("status", false);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @PutMapping(value = "/exp")
    @Operation(description = "회원 경험치 변동")
    public ResponseEntity<?> modifyMemberExp(@RequestBody MemberExpRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 경험치 변동");
        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            MemberDTO findMember = memberService.findMember(memberId);
            MemberDTO updatedMember = memberService.memberSave(Member.builder()
                    .memberId(findMember.getMemberId())
                    .exp(request.getExp()).build());

//            result.put("memberId", updatedMember.getMemberId());
//            result.put("exp", updatedMember.getExp());
//            result.put("status", true);
//
//            return new ResponseEntity<>(result, HttpStatus.CREATED);
            return new ResponseEntity<>(MemberExpResponse.create(updatedMember, true), HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {
            log.info(e.getMessage());
            result.put("message", e.getMessage());
            result.put("status", false);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/profile")
    @Operation(description = "회원 프로필 불러오기")
    public ResponseEntity<?> getMemberProfile(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 불러오기");
        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            MemberDTO member = memberService.findMember(memberId);
            result.put("profile", member);
            result.put("status", true);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
            result.put("message", e.getMessage());
            result.put("status", false);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @PutMapping("/profile")
    @Operation(description = "회원 프로필 수정하기")
    public ResponseEntity<?> modifyMemberProfile(@RequestBody MemberModifyRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 수정하기");
        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            List<OTTView> ottViewList = new ArrayList<>();
            // 관심 OTT 저장하기 (Ott 없는 경우 있음)
            for (int ottId : request.getOttId()) {
                // ott 내용 조회 Ott Service단으로 만들기
                ottViewList.add(memberService.findByOttId(ottId));
            }

            MemberDTO findMember = memberService.findMember(memberId);
            MemberDTO updatedMember = memberService.memberSave(Member.builder()
                    .memberId(findMember.getMemberId())
                    .nickname(request.getNickname())
                    .ottView(ottViewList)
                    .partyInviteYn(request.getPartyInviteYn())
                    .build());

            // 닉네임이 변경된 경우에만 JWT 토큰 새로 갱신 및 Redis에 리프레시 토큰 저장
            if (!findMember.getNickname().equals(request.getNickname())) {
                String accessToken = jwtUtil.generateAccessToken(updatedMember.getMemberId(), updatedMember.getNickname());
                String refreshToken = jwtUtil.generateRefreshToken(updatedMember.getMemberId(), updatedMember.getNickname());

                servletResponse.setHeader("Authorization", "Bearer " + accessToken);
                redisUtil.setData(updatedMember.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
            }

            result.put("memberId", updatedMember.getMemberId());
            result.put("nickname", updatedMember.getNickname());
            result.put("ottView", updatedMember.getOttViewList());
            result.put("partyInviteYn", updatedMember.getPartyInviteYn());

            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {
            log.info(e.getMessage());
            result.put("status", false);
            result.put("message", e.getMessage());

            return new ResponseEntity<>(result, HttpStatus.OK);
        }

    }

    @DeleteMapping(value = "/profile")
    @Operation(description = "회원 탈퇴")
    public ResponseEntity<?> deleteMember(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 탈퇴");
        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            memberService.deleteMember(memberId);

            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        } catch (FailDeleteMemberException e) {
            log.info(e.getMessage());
            result.put("status", false);
            result.put("message", e.getMessage());

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/logout")
    @Operation(description = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 로그아웃");
        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            boolean logout = memberService.logout(memberId);
            result.put("status", logout);
            result.put("message", "로그아웃 되었습니다.");

            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        } catch (FailLogoutMemberException e) {
            log.info(e.getMessage());
            result.put("status", false);
            result.put("message", e.getMessage());

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }
}
