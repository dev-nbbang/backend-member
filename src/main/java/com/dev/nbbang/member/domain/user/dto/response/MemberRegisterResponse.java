package com.dev.nbbang.member.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegisterResponse {
    private String memberId;
    private boolean registerYn;
    private boolean status;

    public static MemberRegisterResponse create(String memberId, boolean registerYn, boolean status) {
        return MemberRegisterResponse.builder()
                .memberId(memberId)
                .registerYn(registerYn)
                .status(status)
                .build();
    }

}
