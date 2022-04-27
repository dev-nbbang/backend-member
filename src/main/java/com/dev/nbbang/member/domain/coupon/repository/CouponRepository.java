package com.dev.nbbang.member.domain.coupon.repository;

import com.dev.nbbang.member.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    List<Coupon> findAll();
    Optional<Coupon> findByCouponId(int couponId);
    Coupon save(Coupon coupon);
    void deleteByCouponId(int couponId);
}
