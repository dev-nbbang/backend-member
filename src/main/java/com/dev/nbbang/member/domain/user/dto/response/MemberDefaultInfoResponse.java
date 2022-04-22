package com.dev.nbbang.member.domain.user.dto.response;


import com.dev.nbbang.member.domain.memberott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.memberott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<MemberOtt> memberOtt;
    private boolean status;

    public static MemberDefaultInfoResponse create(MemberDTO member, List<MemberOttDTO> memberOtt, boolean status) {
        return MemberDefaultInfoResponse.builder().memberId(member.getMemberId())
                .nickname(member.getNickname())
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .memberOtt(member.getMemberOtt())
                .status(status).build();
    }
}
