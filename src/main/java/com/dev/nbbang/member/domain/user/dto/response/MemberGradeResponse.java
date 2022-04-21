package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberGradeResponse {
    private String memberId;
    private Grade grade;
    private boolean status;

    public static MemberGradeResponse create(MemberDTO member, boolean status) {
        return MemberGradeResponse.builder()
                .memberId(member.getMemberId())
                .grade(member.getGrade())
                .status(status).build();
    }
}
