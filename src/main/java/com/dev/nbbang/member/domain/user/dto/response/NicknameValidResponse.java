package com.dev.nbbang.member.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NicknameValidResponse {
    private boolean nicknameDup;

    @Builder
    public NicknameValidResponse(boolean nicknameDup) {
        this.nicknameDup = nicknameDup;
    }

    public static NicknameValidResponse create(boolean nicknameDup) {
        return NicknameValidResponse.builder()
                .nicknameDup(nicknameDup)
                .build();
    }
}
