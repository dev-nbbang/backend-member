package com.dev.nbbang.member.domain.user.dto.request;

import com.dev.nbbang.member.domain.user.entity.Grade;
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
}
