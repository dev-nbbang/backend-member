package com.dev.nbbang.member.domain.coupon.entity;

import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "MEMBER_COUPON")
public class MemberCoupon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_coupon_id")
    private Integer memberCouponId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name="coupon_id")
    private Coupon coupon;
    @Column(name="use_yn")
    private String useYN;
    @Column(name="start_ymd")
    private Date startYmd;
    @Column(name="expire_ymd")
    private Date expireYmd;

    public void updateMemberCoupon(Member member, String useYN) {
        this.member = member;
        this.useYN = useYN;
    }
}
