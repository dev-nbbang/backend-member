package com.dev.nbbang.member.domain.user.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "MEMBER")
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String memberId;

    @Column(nullable = false, name = "nickname")
    private String nickname;

    @Column(nullable = false, name = "bank_id")
    private int bankId;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "grade")
    private String grade;

    @Column(name = "poing")
    private int point;

    @Column(name = "exp")
    private int exp;

    @Column(name = "billing_key")
    private String billingKey;

    @Column(name = "party_invite_yn")
    private char partyInviteYn;

    @Builder
    public Member(String memberId, String nickname, int bankId, String bankAccount, String grade, int point, int exp, String billingKey, char partyInviteYn) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.grade = grade;
        this.point = point;
        this.exp = exp;
        this.billingKey = billingKey;
        this.partyInviteYn = partyInviteYn;
    }
}
