package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.user.api.dto.AuthResponse;
import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.exception.FailCreateAuthUrlException;
import com.dev.nbbang.member.domain.user.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.member.domain.user.api.util.SocialAuthUrl;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.dto.request.MemberExpRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberGradeRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberModifyRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberRegisterRequest;
import com.dev.nbbang.member.domain.user.dto.response.*;
import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.domain.user.exception.FailLogoutMemberException;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.dto.response.CommonFailResponse;
import com.dev.nbbang.member.global.dto.response.CommonStatusResponse;
import com.dev.nbbang.member.global.util.JwtUtil;
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
    private final SocialTypeMatcher socialTypeMatcher;
    private final JwtUtil jwtUtil;


    @GetMapping(value = "/oauth/{socialLoginType}")
    @Operation(summary = "소셜 로그인 인가코드 URL", description = "소셜 로그인 인가코드 URL을 생성한다.")
    public ResponseEntity<?> socialLoginType(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);

        try {
            // PathVariable의 소셜 타입을 인가 코드 URL 생성 (카카오, 구글)
            SocialAuthUrl socialAuthUrl = socialTypeMatcher.findSocialAuthUrlByType(socialLoginType);
            String authUrl = socialAuthUrl.makeAuthorizationUrl();
            return new ResponseEntity<>(AuthResponse.create(authUrl), HttpStatus.OK);
        } catch (IllegalSocialTypeException | FailCreateAuthUrlException e) {
            log.info(" >> [Nbbang Member Controller - signUp] : " + e.getMessage());
            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/oauth/{socialLoginType}/callback")
    @Operation(summary = "동의 정보 인증 후 리다이렉트", description = "동의 정보 인증 후 리다이렉트 URI")
    public ResponseEntity<?> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                      @RequestParam(name = "code") String code, HttpServletResponse servletResponse) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);

        // 소셜 로그인 실패시
        String memberId = memberService.socialLogin(socialLoginType, code);
        if (memberId == null) {
            log.info("badRequest");
            // Message 넘기기
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            MemberDTO findMember = memberService.findMember(memberId);

            // 회원 닉네임 수정 시 JWT 새로 생성 및 레디스 값 갱신 (프론트 구현 후 넣어주기)
            String accessToken = memberService.manageToken(findMember);
            servletResponse.setHeader("Authorization", "Bearer " + accessToken);
            System.out.println("accessToken = " + accessToken);

            return new ResponseEntity<>(MemberDefaultInfoResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
            log.info("회원가입필요");

            return new ResponseEntity<>(MemberRegisterResponse.create(memberId, false, false), HttpStatus.OK);
        }
    }

    @PostMapping("/new")
    @Operation(summary = "추가 회원 가입", description = "추가 회원 가입")
    public ResponseEntity<?> signUp(@RequestBody MemberRegisterRequest request, HttpServletResponse servletResponse) {
        try {
            // 요청 데이터 엔티이에 저장
            MemberDTO savedMember = memberService.saveMember(MemberRegisterRequest.toEntity(request), request.getOttId());

            // 회원 생성이 완료된 경우
            String accessToken = memberService.manageToken(savedMember);
            servletResponse.setHeader("Authorization", "Bearer " + accessToken);
            log.info("redis 저장 완료");

            return new ResponseEntity<>(MemberDefaultInfoResponse.create(savedMember, true), HttpStatus.CREATED);
        } catch (NoCreateMemberException | NoSuchOttException | NoCreatedMemberOttException e) {
            log.info(" >> [Nbbang Member Controller - signUp] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
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
        try {
            // 닉네임으로 회원 조회
            MemberDTO findMember = memberService.findMemberByNickname(nickname);

            return new ResponseEntity<>(MemberNicknameResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - findRecommendMember] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/nickname/{nickname}")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인")
    public ResponseEntity<?> checkDuplicateNickname(@PathVariable(name = "nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 중복 확인");
        try {
            boolean nicknameDup = memberService.duplicateNickname(nickname);

            return new ResponseEntity<>(CommonStatusResponse.create(nicknameDup), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - checkDuplicateNickname] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/nickname/list/{nickname}")
    @Operation(summary = "닉네임 리스트 가져오기", description = "닉네임 리스트 가져오기")
    public ResponseEntity<?> searchNicknameList(@PathVariable(name = "nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 리스트 가져오기");

        try {
            List<MemberDTO> findMemberList = memberService.findMemberListByNickname(nickname);

            return new ResponseEntity<>(MemberNicknameResponse.createList(findMemberList, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - searchNicknameList] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/grade")
    @Operation(description = "회원 등급 조회")
    public ResponseEntity<?> getMemberGrade(HttpServletRequest servletRequest) {
        log.info(" >>  [Nbbang Member Service] 회원 등급 조회");

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 회원 조회
            MemberDTO findMember = memberService.findMember(memberId);

            return new ResponseEntity<>(MemberGradeResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - getMemberGrade] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @PutMapping(value = "/grade")
    @Operation(description = "회원 등급 수정")
    public ResponseEntity<?> modifyMemberGrade(@RequestBody MemberGradeRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 등급 수정");

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 회원 등급 수정
            MemberDTO updatedMember = memberService.updateGrade(memberId, MemberGradeRequest.toEntity(request));

            return new ResponseEntity<>(MemberGradeResponse.create(updatedMember, true), HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {
            log.info(" >> [Nbbang Member Controller - modifyMemberGrade] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @PutMapping(value = "/exp")
    @Operation(description = "회원 경험치 변동")
    public ResponseEntity<?> modifyMemberExp(@RequestBody MemberExpRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 경험치 변동");

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 회원 경험치 변동
            MemberDTO updatedMember = memberService.updateExp(memberId, MemberExpRequest.toEntity(request));

            return new ResponseEntity<>(MemberExpResponse.create(updatedMember, true), HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {
            log.info(" >> [Nbbang Member Controller - modifyMemberExp] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/profile")
    @Operation(description = "회원 프로필 불러오기")
    public ResponseEntity<?> getMemberProfile(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 불러오기");

        try {
            // 현재 자신만 불러오는 걸로 되었네..?
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 회원 정보 불러오기
            MemberDTO findMember = memberService.findMember(memberId);

            return new ResponseEntity<>(MemberProfileResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - getMemberProfile] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @PutMapping("/profile")
    @Operation(description = "회원 프로필 수정하기")
    public ResponseEntity<?> modifyMemberProfile(@RequestBody MemberModifyRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 수정하기");

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 변경 전 닉네임 가져오기
            MemberDTO findMember = memberService.findMember(memberId);

            // 회원 정보 수정
            MemberDTO updatedMember = memberService.updateMember(memberId, MemberModifyRequest.toEntity(request), request.getOttId());

            // 닉네임이 변경된 경우에만 JWT 토큰 새로 갱신 및 Redis에 리프레시 토큰 저장
            if (!findMember.getNickname().equals(updatedMember.getNickname())) {
                String accessToken = memberService.manageToken(updatedMember);
                servletResponse.setHeader("Authorization", "Bearer " + accessToken);
            }

            return new ResponseEntity<>(MemberModifyResponse.create(updatedMember), HttpStatus.CREATED);
        } catch (NoCreateMemberException | NoSuchOttException | NoCreatedMemberOttException e) {
            log.info(" >> [Nbbang Member Controller - modifyMemberProfile] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }

    }

    // 회원 탈퇴 추후 CASCADE 설정 및 소셜 로그아웃 구현 필요
    @DeleteMapping(value = "/profile")
    @Operation(description = "회원 탈퇴")
    public ResponseEntity<?> deleteMember(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 탈퇴");

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            memberService.deleteMember(memberId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (FailDeleteMemberException e) {
            log.info(" >> [Nbbang Member Controller - deleteMember] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(true, e.getMessage()), HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/logout")
    @Operation(description = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 로그아웃");

        try {
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));
            boolean logout = memberService.logout(memberId);

            log.info("로그아웃 되었습니다 : [logout : " + logout + "]");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (FailLogoutMemberException e) {
            log.info(" >> [Nbbang Member Controller - logout] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }
}
