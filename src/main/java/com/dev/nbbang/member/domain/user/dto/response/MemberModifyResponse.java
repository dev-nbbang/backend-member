package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class MemberModifyResponse {
    private String memberId;
    private String nickname;
    private List<OttView> ottView;
    private String partyInviteYn;

    @Builder
    public MemberModifyResponse(String memberId, String nickname, List<OttView> ottView, String partyInviteYn) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.ottView = ottView;
        this.partyInviteYn = partyInviteYn;
    }

    public static MemberModifyResponse create(MemberDTO member) {
        return MemberModifyResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .ottView(getOttView(member))
                .partyInviteYn(member.getPartyInviteYn())
                .build();
    }

    private static List<OttView> getOttView(MemberDTO member) {
        List<OttView> ottView = new ArrayList<>();
        for (MemberOtt memberOtt : member.getMemberOtt()) {
            ottView.add(memberOtt.getOttView());
        }

        return ottView;
    }
}
