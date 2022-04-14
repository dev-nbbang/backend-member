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
public class NicknameMemberResponse {
    private String memberId;
    private String nickname;

    public static NicknameMemberResponse create(Member member){
        return NicknameMemberResponse.builder().memberId(member.getMemberId()).nickname(member.getNickname()).build();

    }
}
