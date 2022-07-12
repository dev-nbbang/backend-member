package com.dev.nbbang.member.domain.user.dto.request;

import com.dev.nbbang.member.domain.point.dto.request.MemberPointRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDiscRequest {
    Integer couponId;
    Integer couponType;
    MemberPointRequest pointObj;
}