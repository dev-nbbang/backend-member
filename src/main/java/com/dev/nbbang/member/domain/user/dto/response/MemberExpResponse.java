package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberExpResponse {
    private String memberId;
    private Long exp;
    private boolean status;
    private String message;

    public static MemberExpResponse create(MemberDTO member, boolean status, String message) {
        return MemberExpResponse.builder()
                .memberId(member.getMemberId())
                .exp(member.getExp())
                .status(status)
                .message(message)
                .build();
    }
}
