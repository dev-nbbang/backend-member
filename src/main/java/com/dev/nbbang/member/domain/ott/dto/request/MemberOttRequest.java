package com.dev.nbbang.member.domain.ott.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberOttRequest {
    private String memberId;
    private List<Integer> ottId;
}

