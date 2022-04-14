package com.dev.nbbang.member.domain.user.dto.response;


import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
    private String memberId;
    private String nickname;
    private String grade;
    private long point;

    public static MemberResponse create(Member member) {
        return MemberResponse.builder().memberId(member.getMemberId())
                .nickname(member.getNickname())
                .grade(member.getGrade())
                .point(member.getPoint()).build();
    }
}
