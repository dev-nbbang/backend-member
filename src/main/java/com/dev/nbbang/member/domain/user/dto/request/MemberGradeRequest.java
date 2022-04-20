package com.dev.nbbang.member.domain.user.dto.request;

import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberGradeRequest {
    private String memberId;
    private Grade grade;

    public static Member toEntity(MemberGradeRequest request) {
        return Member.builder()
                .memberId(request.getMemberId())
                .grade(request.getGrade())
                .build();
    }
}
