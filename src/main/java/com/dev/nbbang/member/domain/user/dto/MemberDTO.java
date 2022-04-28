package com.dev.nbbang.member.domain.user.dto;

import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberDTO {
    private String memberId;
    private String nickname;
    private Integer bankId;
    private String bankAccount;
    private Grade grade;
    private Long point;
    private Long exp;
    private String billingKey;
    private String partyInviteYn;
    private List<MemberOtt> memberOtt;
    private List<Point> pointList;
    public static MemberDTO create(Member member) {
        return MemberDTO.builder().memberId(member.getMemberId())
                .nickname(member.getNickname())
                .bankId(member.getBankId())
                .bankAccount((member.getBankAccount()))
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .billingKey(member.getBillingKey())
                .partyInviteYn(member.getPartyInviteYn())
                .memberOtt(member.getMemberOtt())
//                .pointList(member.getPointList())
                .build();
    }

    public static List<MemberDTO> createList(List<Member> memberList) {
        List<MemberDTO> memberDTOList = new ArrayList<>();

        for (Member member : memberList) {
            memberDTOList.add(MemberDTO.builder().memberId(member.getMemberId())
                    .nickname(member.getNickname())
                    .bankId(member.getBankId())
                    .bankAccount((member.getBankAccount()))
                    .grade(member.getGrade())
                    .point(member.getPoint())
                    .exp(member.getExp())
                    .billingKey(member.getBillingKey())
                    .partyInviteYn(member.getPartyInviteYn())
                    .memberOtt(member.getMemberOtt())
//                    .pointList(member.getPointList())
                    .build());
        }

        return memberDTOList;
    }
}
