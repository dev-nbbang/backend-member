package com.dev.nbbang.member.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberModifyRequest { ;
    private String nickname;
    private List<Integer> ottId;
    private String partyInviteYn;
}
