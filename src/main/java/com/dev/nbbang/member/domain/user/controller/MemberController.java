package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.dto.request.MemberExpRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberGradeRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberLeaveRequest;
import com.dev.nbbang.member.domain.user.dto.request.MemberModifyRequest;
import com.dev.nbbang.member.domain.user.dto.response.*;
import com.dev.nbbang.member.domain.user.exception.*;
import com.dev.nbbang.member.domain.user.service.MemberProducer;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.dto.response.CommonResponse;
import com.dev.nbbang.member.global.dto.response.CommonSuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/members")
@Slf4j
@Tag(name = "Member", description = "Member API")
public class MemberController {
    private final MemberService memberService;
    private final MemberProducer memberProducer;

    @GetMapping(value = "/recommend/{nickname}")
    @Operation(summary = "닉네임으로 추천인 회원 조회하기", description = "닉네임으로 추천인 회원 조회하기")
    public ResponseEntity<?> findRecommendMember(@PathVariable(name = "nickname") String nickname) {
        log.info(">> [Nbbang Member Service] 닉네임으로 추천인 회원 조회하기");
        try {
            // 닉네임으로 회원 조회
            MemberDTO findMember = memberService.findMemberByNickname(nickname);

            return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberNicknameResponse.create(findMember), "추천인 조회에 성공했습니다."));
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - findRecommendMember] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @GetMapping(value = "/nickname/{nickname}")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인")
    public ResponseEntity<?> checkDuplicateNickname(@PathVariable(name = "nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 중복 확인");
        try {
            boolean nicknameDup = memberService.duplicateNickname(nickname);

            return ResponseEntity.ok(CommonSuccessResponse.response(true, nicknameDup, "사용 가능한 닉네임입니다."));
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - checkDuplicateNickname] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @GetMapping(value = "/nickname/list/{nickname}")
    @Operation(summary = "닉네임 리스트 가져오기", description = "닉네임 리스트 가져오기")
    public ResponseEntity<?> searchNicknameList(@PathVariable(name = "nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 리스트 가져오기");

        try {
            List<MemberDTO> findMemberList = memberService.findMemberListByNickname(nickname);

            // 리스트 상태값 고민
            return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberNicknameResponse.createList(findMemberList), "닉네임 리스트 조회에 성공했습니다."));
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - searchNicknameList] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @GetMapping(value = "/grade")
    @Operation(description = "회원 등급 조회")
    public ResponseEntity<?> getMemberGrade(HttpServletRequest servletRequest) {
        log.info(" >>  [Nbbang Member Service] 회원 등급 조회");

        try {
            String memberId = servletRequest.getHeader("X-Authorization-Id");

            // 회원 조회
            MemberDTO findMember = memberService.findMember(memberId);

            return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberGradeResponse.create(findMember), "회원 등급 조회에 성공했습니다."));
        } catch (NoSuchMemberException e) {


            log.info(" >> [Nbbang Member Controller - getMemberGrade] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @PutMapping(value = "/grade")
    @Operation(description = "회원 등급 수정")
    public ResponseEntity<?> modifyMemberGrade(@RequestBody MemberGradeRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 등급 수정");

        try {
            String memberId = servletRequest.getHeader("X-Authorization-Id");

            // 회원 등급 수정
            MemberDTO updatedMember = memberService.updateGrade(memberId, MemberGradeRequest.toEntity(request));

            // 여기 부분 MemberGradeResponse랑 CommonSucessResponse 확인해서 리팩토링 진행하면 될듯!
            return new ResponseEntity<>(CommonSuccessResponse.response(true, MemberGradeResponse.create(updatedMember), "회원 등급 수정에 성공했습니다."), HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {

            log.info(" >> [Nbbang Member Controller - modifyMemberGrade] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @PutMapping(value = "/exp")
    @Operation(description = "회원 경험치 변동")
    public ResponseEntity<?> modifyMemberExp(@RequestBody MemberExpRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 경험치 변동");

        try {
            String memberId = servletRequest.getHeader("X-Authorization-Id");

            // 회원 경험치 변동
            MemberDTO updatedMember = memberService.updateExp(memberId, MemberExpRequest.toEntity(request));

            return new ResponseEntity<>(CommonSuccessResponse.response(true, MemberExpResponse.create(updatedMember) , "회원 경험치 변경에 성공했습니다."), HttpStatus.CREATED);
        } catch (NoCreateMemberException e) {
            log.info(" >> [Nbbang Member Controller - modifyMemberExp] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @GetMapping(value = "/profile")
    @Operation(description = "회원 프로필 불러오기")
    public ResponseEntity<?> getMemberProfile(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 불러오기");

        try {
            // 현재 자신만 불러오는 걸로 되었네..?
            String memberId = servletRequest.getHeader("X-Authorization-Id");

            // 회원 정보 불러오기
            MemberDTO findMember = memberService.findMember(memberId);

            return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberProfileResponse.create(findMember), "회원 프로필 조회에 성공했습니다."));
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Member Controller - getMemberProfile] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @Operation(description = "회원 프로필 수정하기")
    public ResponseEntity<?> modifyMemberProfile(@RequestBody MemberModifyRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 수정하기");

        try {
            // X-Authorization-id
            String memberId = servletRequest.getHeader("X-Authorization-Id");

            // 회원 정보 수정
            MemberDTO updatedMember = memberService.updateMember(memberId, MemberModifyRequest.toEntity(request), request.getOttId());

            return new ResponseEntity<>(CommonSuccessResponse.response(true, MemberModifyResponse.create(updatedMember), "회원 프로필 수정에 성공했습니다."), HttpStatus.CREATED);
        } catch (NoCreateMemberException | NoSuchOttException | NoCreatedMemberOttException e) {
            log.info(" >> [Nbbang Member Controller - modifyMemberProfile] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }

    }

    // 회원 탈퇴 추후 CASCADE 설정 및 소셜 로그아웃 구현 필요
    @DeleteMapping(value = "/profile")
    @Operation(description = "회원 탈퇴")
    public ResponseEntity<?> deleteMember(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 탈퇴");

        try {
            String memberId = servletRequest.getHeader("X-Authorization-Id");

            // 회원 탈퇴 로직
            memberService.deleteMember(memberId);

            // 회원 탈퇴 로직 성공 시 회원 탈퇴 이벤트 발행
            memberProducer.sendLeaveMemberMessage(MemberLeaveRequest.create(memberId));

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (FailDeleteMemberException | JsonProcessingException e) {
            log.info(" >> [Nbbang Member Controller - deleteMember] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @DeleteMapping(value = "/logout")
    @Operation(description = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 로그아웃");

        try {
            String memberId = servletRequest.getHeader("X-Authorization-Id");
            boolean logout = memberService.logout(memberId);

            log.info("로그아웃 되었습니다 : [logout : " + logout + "]");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (FailLogoutMemberException e) {
            log.info(" >> [Nbbang Member Controller - logout] : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }
}
