package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
public class MemberExpResponse {
    private String memberId;
    private Long exp;

    @Builder
    public MemberExpResponse(String memberId, Long exp) {
        this.memberId = memberId;
        this.exp = exp;
    }

    public static MemberExpResponse create(MemberDTO member)  {
        return MemberExpResponse.builder()
                .memberId(member.getMemberId())
                .exp(member.getExp())
                .build();
    }
}
