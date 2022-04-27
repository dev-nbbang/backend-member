package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberExpResponse {
    private String memberId;
    private Long exp;
    private boolean status;

    public static MemberExpResponse create(MemberDTO member, boolean status) {
        return MemberExpResponse.builder()
                .memberId(member.getMemberId())
                .exp(member.getExp())
                .status(status)
                .build();
    }
}
