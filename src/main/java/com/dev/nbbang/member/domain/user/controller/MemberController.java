package com.dev.nbbang.member.domain.user.controller;

import com.dev.nbbang.member.domain.coupon.exception.AlreadyUsedCouponException;
import com.dev.nbbang.member.domain.coupon.exception.NoSuchCouponException;
import com.dev.nbbang.member.domain.coupon.service.CouponService;
import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.point.dto.request.MemberPointRequest;
import com.dev.nbbang.member.domain.point.exception.NoCreatedPointDetailsException;
import com.dev.nbbang.member.domain.point.service.PointService;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.dto.request.*;
import com.dev.nbbang.member.domain.user.dto.response.*;
import com.dev.nbbang.member.domain.user.exception.*;
import com.dev.nbbang.member.domain.user.service.MemberProducer;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.dto.response.CommonResponse;
import com.dev.nbbang.member.global.dto.response.CommonStatusResponse;
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
    private final PointService pointService;
    private final CouponService couponService;

    @GetMapping(value = "/recommend")
    @Operation(summary = "닉네임으로 추천인 회원 조회하기", description = "닉네임으로 추천인 회원 조회하기")
    public ResponseEntity<?> findRecommendMember(@RequestParam("nickname")String nickname) {
        log.info(">> [Nbbang Member Service] 닉네임으로 추천인 회원 조회하기");
        // 닉네임으로 회원 조회
        MemberDTO findMember = memberService.findMemberByNickname(nickname);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberNicknameResponse.create(findMember), "추천인 조회에 성공했습니다."));

    }

    @GetMapping(value = "/nickname")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인")
    public ResponseEntity<?> checkDuplicateNickname(@RequestParam("nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 중복 확인확인");
        boolean nicknameDup = memberService.duplicateNickname(nickname);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, nicknameDup, "사용 가능한 닉네임입니다."));

    }

    @GetMapping(value = "/nickname/list")
    @Operation(summary = "닉네임 리스트 가져오기", description = "닉네임 리스트 가져오기")
    public ResponseEntity<?> searchNicknameList(@RequestParam("nickname") String nickname) {
        log.info(" >> [Nbbang Member Service] 닉네임 리스트 가져오기");

        List<MemberDTO> findMemberList = memberService.findMemberListByNickname(nickname);

        // 리스트 상태값 고민
        return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberNicknameResponse.createList(findMemberList), "닉네임 리스트 조회에 성공했습니다."));

    }

    @GetMapping(value = "/grade")
    @Operation(description = "회원 등급 조회")
    public ResponseEntity<?> getMemberGrade(HttpServletRequest servletRequest) {
        log.info(" >>  [Nbbang Member Service] 회원 등급 조회");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 회원 조회
        MemberDTO findMember = memberService.findMember(memberId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberGradeResponse.create(findMember), "회원 등급 조회에 성공했습니다."));

    }

    @PutMapping(value = "/grade")
    @Operation(description = "회원 등급 수정")
    public ResponseEntity<?> modifyMemberGrade(@RequestBody MemberGradeRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 등급 수정");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 회원 등급 수정
        MemberDTO updatedMember = memberService.updateGrade(memberId, MemberGradeRequest.toEntity(request));

        // 여기 부분 MemberGradeResponse랑 CommonSucessResponse 확인해서 리팩토링 진행하면 될듯!
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonSuccessResponse.response(true, MemberGradeResponse.create(updatedMember), "회원 등급 수정에 성공했습니다."));
    }

    @PutMapping(value = "/exp")
    @Operation(description = "회원 경험치 변동")
    public ResponseEntity<?> modifyMemberExp(@RequestBody MemberExpRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 경험치 변동");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 회원 경험치 변동
        MemberDTO updatedMember = memberService.updateExp(memberId, MemberExpRequest.toEntity(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonSuccessResponse.response(true, MemberExpResponse.create(updatedMember), "회원 경험치 변경에 성공했습니다."));
    }

    @GetMapping(value = "/profile")
    @Operation(description = "회원 프로필 불러오기")
    public ResponseEntity<?> getMemberProfile(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 불러오기");

        // 현재 자신만 불러오는 걸로 되었네..?
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 회원 정보 불러오기
        MemberDTO findMember = memberService.findMember(memberId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberProfileResponse.create(findMember), "회원 프로필 조회에 성공했습니다."));
    }

    @PutMapping("/profile")
    @Operation(description = "회원 프로필 수정하기")
    public ResponseEntity<?> modifyMemberProfile(@RequestBody MemberModifyRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 회원 프로필 수정하기");

        // X-Authorization-id
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 회원 정보 수정
        MemberDTO updatedMember = memberService.updateMember(memberId, MemberModifyRequest.toEntity(request), request.getOttId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonSuccessResponse.response(true, MemberModifyResponse.create(updatedMember), "회원 프로필 수정에 성공했습니다."));
    }

    // 회원 탈퇴 추후 CASCADE 설정 및 소셜 로그아웃 구현 필요
    @DeleteMapping(value = "/profile")
    @Operation(description = "회원 탈퇴")
    public ResponseEntity<?> deleteMember(HttpServletRequest servletRequest) throws JsonProcessingException {
        log.info(" >> [Nbbang Member Service] 회원 탈퇴");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 회원 탈퇴 로직
        memberService.deleteMember(memberId);

        // 회원 탈퇴 로직 성공 시 회원 탈퇴 이벤트 발행
        memberProducer.sendLeaveMemberMessage(MemberLeaveRequest.create(memberId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/logout")
    @Operation(description = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Member Service] 로그아웃");

        String memberId = servletRequest.getHeader("X-Authorization-Id");
        boolean logout = memberService.logout(memberId);

        log.info("로그아웃 되었습니다 : [logout : " + logout + "]");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = "/discount")
    @Operation(description = "할인 로직")
    public ResponseEntity<?> discount(@RequestBody MemberDiscRequest memberDiscRequest) {
        String memberId = memberDiscRequest.getPointObj().getMemberId();
        MemberDTO memberDTO = memberService.findMember(memberId);
        Long point = memberDiscRequest.getPointObj().getUsePoint();
        if(memberDiscRequest.getCouponId() != null) {
            try {
                couponService.updateMemberCoupon(memberId, memberDiscRequest.getCouponId());
            } catch (NoSuchCouponException | NoSuchMemberException | AlreadyUsedCouponException e) {
                log.info(e.getMessage());
                return ResponseEntity.ok(CommonStatusResponse.create(false));
            }
        }
        if(point != null && memberDTO.getPoint() >= point) {
            try {
                pointService.updatePoint(memberId, MemberPointRequest.toDTO(memberDiscRequest.getPointObj()));
            } catch (NoSuchCouponException | NoSuchMemberException | NoCreatedPointDetailsException e) {
                log.info(e.getMessage());
                return ResponseEntity.ok(CommonStatusResponse.create(false));
            }
        }
        return ResponseEntity.ok(CommonStatusResponse.create(true));
    }
}
