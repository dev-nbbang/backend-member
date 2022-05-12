package com.dev.nbbang.member.domain.coupon.controller;

import com.dev.nbbang.member.domain.coupon.dto.request.CouponRequest;
import com.dev.nbbang.member.domain.coupon.dto.response.MemberCouponResponse;
import com.dev.nbbang.member.domain.coupon.entity.MemberCoupon;
import com.dev.nbbang.member.domain.coupon.exception.NoSuchCouponException;
import com.dev.nbbang.member.domain.coupon.service.CouponService;
import com.dev.nbbang.member.global.dto.response.CommonResponse;
import com.dev.nbbang.member.global.dto.response.CommonStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/coupon")
public class CouponController {
    private final CouponService couponService;
    //사용자 영역
    //조회
    @GetMapping(value = "/")
    @Operation(description = "사용자의 쿠폰리스트 조회")
    public ResponseEntity<?> couponList(HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        try {
            List<MemberCoupon> memberCouponList = couponService.memberCouponList(memberId);
            return new ResponseEntity<>(MemberCouponResponse.createList(memberCouponList), HttpStatus.OK);
        } catch (NoSuchCouponException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(false), HttpStatus.OK);
    }
    //저장
    @PostMapping(value = "/new")
    @Operation
    public ResponseEntity<?> saveCoupon(@RequestBody CouponRequest couponRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        boolean status = false;
        try {
            couponService.saveMemberCoupon(memberId, couponRequest.getCouponId());
            return new ResponseEntity<>(CommonResponse.create(true, "쿠폰 저장했습니다"), HttpStatus.CREATED);
        } catch (NoSuchCouponException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "쿠폰 저장에 실패했습니다"), HttpStatus.OK);
    }
    //사용
    @PutMapping(value = "/{couponId}")
    @Operation(description = "사용자의 쿠폰 사용 처리")
    public ResponseEntity<?> useCoupon(@PathVariable int couponId, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        boolean status = false;
        try {
            couponService.updateMemberCoupon(memberId, couponId);
            return new ResponseEntity<>(CommonResponse.create(true, "쿠폰을 사용했습니다"), HttpStatus.CREATED);
        } catch (NoSuchCouponException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "쿠폰 사용에 실패했습니다"), HttpStatus.OK);
    }

    //삭제
    @DeleteMapping(value = "/{couponId}")
    @Operation(description = "사용자의 쿠폰 삭제")
    public ResponseEntity<?> deleteCoupon(@PathVariable int couponId, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        try {
            couponService.deleteMemberCoupon(memberId, couponId);
            return new ResponseEntity<>(CommonResponse.create(true, "쿠폰 삭제를 완료했습니다"), HttpStatus.NO_CONTENT);
        } catch (NoSuchCouponException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "쿠폰 삭제에 실패했습니다"), HttpStatus.OK);
    }

    //관리자 영역
    //전제 쿠폰 조회
    //쿠폰 조회
    //저장
    //수정
    //삭제

}
