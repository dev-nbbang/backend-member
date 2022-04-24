package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.memberott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileResponse {
    private String memberId;
    private String nickname;
    private Integer bankId;
    private String bankAccount;
    private Grade grade;
    private Long point;
    private Long exp;
    private String billingKey;
    private String partyInviteYn;
    private List<OttView> ottView;
    private boolean status;

    public static MemberProfileResponse create(MemberDTO member, boolean status) {
        return MemberProfileResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .bankId(member.getBankId())
                .bankAccount(member.getBankAccount())
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .partyInviteYn(member.getPartyInviteYn())
                .ottView(getOttView(member))
                .status(status).build();
    }

    private static List<OttView> getOttView(MemberDTO member) {
        List<OttView> ottView = new ArrayList<>();

        for (MemberOtt memberOtt : member.getMemberOtt()) {
            ottView.add(memberOtt.getOttView());
        }

        return ottView;
    }
}
