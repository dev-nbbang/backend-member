package com.dev.nbbang.member.domain.user.dto.response;


import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private String memberId;
    private String nickname;
    private String grade;
    private long point;

    public static MemberResponse create(Member member) {
        // 여기서 null로 리턴해보림...
        if(member == null) return null;
        return new MemberResponse(member.getMemberId(), member.getNickname(), member.getGrade(), member.getPoint());
    }
}
