package com.dev.nbbang.member.domain.user.entity;

import com.dev.nbbang.member.domain.coupon.entity.MemberCoupon;
import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@Builder
@Table(name = "MEMBER")
@EqualsAndHashCode
public class Member implements UserDetails {
    @Id
    @Column(name = "MEMBER_ID", nullable = false)
    private String memberId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "bank_id")
    private Integer bankId;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "grade")
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "point")
    private Long point;

    @Column(name = "exp")
    private Long exp;

    @Column(name = "billing_key")
    private String billingKey;

    @Column(name = "party_invite_yn")
    private String partyInviteYn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default        //NPE 해결
    private List<MemberOtt> memberOtt = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<MemberCoupon> memberCouponList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Point> pointList = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (this.grade == null) grade = Grade.BRONZE;
        if (this.point == null) point = 0L;
        if (this.exp == null) exp = 0L;
        if (this.partyInviteYn == null) partyInviteYn = "Y";
    }

    // 회원 등급 수정
    public void updateMember(String memberId, Grade grade) {
        this.memberId = memberId;
        this.grade = grade;
    }

    // 회원 정보 수정
    public void updateMember(String memberId, String nickname, String partyInviteYn, List<MemberOtt> memberOtt) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.partyInviteYn = partyInviteYn;

        //  MEMBER_OTT 관계 제거 후 새로운 데이터 수정
        this.memberOtt.clear();
        this.memberOtt.addAll(memberOtt);
    }

    // 회원 경험치 변경
    public void updateMember(String memberId, Long exp) {
        this.memberId = memberId;
        this.exp = exp;
    }

    // 회원 계좌 수정
    public void updateAccountMember(String memberId, Integer bankId, String bankAccount) {
        this.memberId = memberId;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
    }

    // 회원 빌링키 수정
    public void updateAccountMember(String memberId, String billingKey) {
        this.memberId = memberId;
        this.billingKey = billingKey;
    }

    // 회원 포인트 수정
    public void updatePoint(String memberId, Long updatePoint, PointType pointType) {
        this.memberId = memberId;
        if(pointType == PointType.INCREASE) this.point += updatePoint;
        if(pointType == PointType.DECREASE) this.point -= updatePoint;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return memberId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
