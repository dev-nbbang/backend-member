package com.dev.nbbang.member.domain.coupon.controller;

import com.dev.nbbang.member.domain.coupon.dto.request.CouponRequest;
import com.dev.nbbang.member.domain.coupon.dto.response.MemberCouponResponse;
import com.dev.nbbang.member.domain.coupon.entity.MemberCoupon;
import com.dev.nbbang.member.domain.coupon.exception.NoSuchCouponException;
import com.dev.nbbang.member.domain.coupon.service.CouponService;
import com.dev.nbbang.member.global.dto.response.CommonStatusResponse;
import com.dev.nbbang.member.global.util.JwtUtil;
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
    private final JwtUtil jwtUtil;
    //사용자 영역
    //조회
    @GetMapping(value = "/")
    @Operation(description = "사용자의 쿠폰리스트 조회")
    public ResponseEntity<?> couponList(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
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
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        boolean status = false;
        try {
            couponService.saveMemberCoupon(memberId, couponRequest.getCouponId());
            status = true;
        } catch (NoSuchCouponException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }
    //사용
    @PutMapping(value = "/{couponId}")
    @Operation(description = "사용자의 쿠폰 사용 처리")
    public ResponseEntity<?> useCoupon(@PathVariable int couponId, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        boolean status = false;
        try {
            couponService.updateMemberCoupon(memberId, couponId);
            status = true;
        } catch (NoSuchCouponException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    //삭제
    @DeleteMapping(value = "/{couponId}")
    @Operation(description = "사용자의 쿠폰 삭제")
    public ResponseEntity<?> deleteCoupon(@PathVariable int couponId, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        boolean status = false;
        try {
            couponService.deleteMemberCoupon(memberId, couponId);
            status = true;
        } catch (NoSuchCouponException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    //관리자 영역
    //전제 쿠폰 조회
    //쿠폰 조회
    //저장
    //수정
    //삭제

}
