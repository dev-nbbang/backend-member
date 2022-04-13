package com.dev.nbbang.member.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReq {
    private String memberId;
    private String nickname;
    private String billingKey;
    private List<Integer> ottId;
}
