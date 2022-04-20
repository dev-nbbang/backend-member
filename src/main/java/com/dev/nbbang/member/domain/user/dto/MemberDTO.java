package com.dev.nbbang.member.domain.user.dto;

import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
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
    private List<OTTView> ottView;

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
                .ottView(member.getOttView()).build();
    }

    public static List<MemberDTO> createList(List<Member> findMemberList) {
        List<MemberDTO> memberDTOList = new ArrayList<>();

        for (Member member : findMemberList) {
            memberDTOList.add(MemberDTO.builder().memberId(member.getMemberId())
                    .nickname(member.getNickname())
                    .bankId(member.getBankId())
                    .bankAccount((member.getBankAccount()))
                    .grade(member.getGrade())
                    .point(member.getPoint())
                    .exp(member.getExp())
                    .billingKey(member.getBillingKey())
                    .partyInviteYn(member.getPartyInviteYn())
                    .ottView(member.getOttView()).build());
        }

        return memberDTOList;
    }
}
