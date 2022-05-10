package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class MemberGradeResponse {
    private String memberId;
    private Grade grade;

    @Builder
    public MemberGradeResponse(String memberId, Grade grade) {
        this.memberId = memberId;
        this.grade = grade;
    }

    public static MemberGradeResponse create(MemberDTO member) {
        return MemberGradeResponse.builder()
                .memberId(member.getMemberId())
                .grade(member.getGrade())
                .build();
    }
}
