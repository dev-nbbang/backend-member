package com.dev.nbbang.member.domain.coupon.dto.response;

import com.dev.nbbang.member.domain.coupon.entity.Coupon;
import com.dev.nbbang.member.domain.coupon.entity.MemberCoupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberCouponResponse {
    private Integer couponId;
    private Integer couponType;
    private String couponName;
    private Date startYmd;
    private Date expireYmd;

    public static List<MemberCouponResponse> createList(List<MemberCoupon> memberCouponList) {
        List<MemberCouponResponse> response = new ArrayList<>();
        for (MemberCoupon memberCoupon : memberCouponList) {
            Coupon coupon = memberCoupon.getCoupon();
            response.add(MemberCouponResponse.builder()
                    .couponId(coupon.getCouponId())
                    .couponType(coupon.getCouponType())
                    .couponName(coupon.getCouponName())
                    .startYmd(memberCoupon.getStartYmd())
                    .expireYmd(memberCoupon.getExpireYmd())
                    .build());
        }
        return response;
    }
}
