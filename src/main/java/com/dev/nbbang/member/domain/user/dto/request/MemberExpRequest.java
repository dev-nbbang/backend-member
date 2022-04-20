package com.dev.nbbang.member.domain.user.dto.request;

import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberExpRequest {
    private String memberId;
    private Long exp;

    public static Member toEntity(MemberExpRequest request) {
        return Member.builder()
                .memberId(request.getMemberId())
                .exp(request.getExp())
                .build();
    }
}
