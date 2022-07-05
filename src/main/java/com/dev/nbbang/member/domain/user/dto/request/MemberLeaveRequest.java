package com.dev.nbbang.member.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLeaveRequest {
    private String memberId;

    @Builder
    public MemberLeaveRequest(String memberId) {
        this.memberId = memberId;
    }

    public static MemberLeaveRequest create(String memberId) {
        return MemberLeaveRequest.builder()
                .memberId(memberId)
                .build();
    }
}
