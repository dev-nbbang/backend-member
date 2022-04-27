package com.dev.nbbang.member.domain.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "COUPON")
public class Coupon {
    @Id
    @Column(name="coupon_id", nullable = false)
    Integer couponId;
    @Column(name="expire_day")
    Integer expireDay;
    @Column(name="coupon_type")
    Integer couponType;
    @Column(name="reg_ymd")
    Timestamp regYMD;
    @Column(name="coupon_name")
    String couponName;

}
