package com.dev.nbbang.member.domain.user.dto.response;


import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDefaultInfoResponse {
    private String memberId;
    private String nickname;
    private Grade grade;
    private Long point;
    private Long exp;
    private List<OttView> ottView;
    private boolean isRegister;
    private String message;

    public static MemberDefaultInfoResponse create(MemberDTO member, boolean isRegister, String message) {
        return MemberDefaultInfoResponse.builder().memberId(member.getMemberId())
                .nickname(member.getNickname())
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .ottView(getOttView(member))
                .isRegister(isRegister)
                .message(message)
                .build();
    }

    private static List<OttView> getOttView(MemberDTO member) {
        List<OttView> ottView = new ArrayList<>();
        for (MemberOtt memberOtt : member.getMemberOtt()) {
            ottView.add(memberOtt.getOttView());
        }

        return ottView;
    }
}
