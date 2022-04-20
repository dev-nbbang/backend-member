package com.dev.nbbang.member.domain.user.dto.request;

import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberModifyRequest {
    private String memberId;
    private String nickname;
    private List<Integer> ottId;
    private String partyInviteYn;


    public static Member toEntity(MemberModifyRequest request, List<OTTView> ottView) {
        return Member.builder()
                .memberId(request.getMemberId())
                .nickname(request.getNickname())
                .ottView(ottView)
                .partyInviteYn(request.getPartyInviteYn())
                .build();
    }
}
