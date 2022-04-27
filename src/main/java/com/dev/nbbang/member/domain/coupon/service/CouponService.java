package com.dev.nbbang.member.domain.coupon.service;

import com.dev.nbbang.member.domain.coupon.entity.Coupon;
import com.dev.nbbang.member.domain.coupon.entity.MemberCoupon;

import java.util.List;

public interface CouponService {
    //사용자의 쿠폰 리스트 조회
    List<MemberCoupon> memberCouponList(String memberId);
    //사용자의 쿠폰 저장
    void saveMemberCoupon(String memberId, int couponId);
    //사용자의 쿠폰 사용 처리
    void updateMemberCoupon(String memberId, int couponId);
    //사용자의 쿠폰 삭제
    void deleteMemberCoupon(String memberId, int couponId);

    //관리자 영역
    //쿠폰 전체 조회
    List<Coupon> findCouponList();
    //쿠폰 조회
    Coupon findCoupon(int couponId);
    //쿠폰 등록,수정
    void save(Coupon coupon);
    //쿠폰 삭제
    void deleteCoupon(int couponId);
}
