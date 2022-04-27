package com.dev.nbbang.member.domain.coupon.repository;

import com.dev.nbbang.member.domain.coupon.entity.Coupon;
import com.dev.nbbang.member.domain.coupon.entity.MemberCoupon;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Integer> {
    Optional<List<MemberCoupon>> findAllByMemberAndUseYN(Member member, String useYN);
    Optional<MemberCoupon> findByMemberAndCoupon(Member member, Coupon coupon);
    MemberCoupon save(MemberCoupon memberCoupon);
    void deleteByMemberAndCoupon(Member member, Coupon coupon);
}
