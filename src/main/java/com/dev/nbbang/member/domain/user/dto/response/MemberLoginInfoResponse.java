package com.dev.nbbang.member.domain.user.dto.response;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginInfoResponse {
    private String memberId;
    private String nickname;
    private Grade grade;
    private Long point;
    private Long exp;

    @Builder
    public MemberLoginInfoResponse(String memberId, String nickname, Grade grade, Long point, Long exp) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.grade = grade;
        this.point = point;
        this.exp = exp;
    }

    @Builder
    public static MemberLoginInfoResponse create(MemberDTO member) {
        return MemberLoginInfoResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .build();
    }
}
