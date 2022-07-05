package com.dev.nbbang.member.domain.user.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberNicknameRequest {
    private String nickname;

    @Builder
    public MemberNicknameRequest(String nickname) {
        this.nickname = nickname;
    }
}
