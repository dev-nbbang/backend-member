package com.dev.nbbang.member.domain.memberott.dto;

import com.dev.nbbang.member.domain.memberott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
public class MemberOttDTO {
    private Member member;
    private OttView ottView;

    public static MemberOttDTO create(MemberOtt memberOtt) {
        return MemberOttDTO.builder()
                .member(memberOtt.getMember())
                .ottView(memberOtt.getOttView())
                .build();
    }

    public static  List<MemberOttDTO> createList(List<MemberOtt> memberOttList) {
        List<MemberOttDTO> memberOttDTOList = new ArrayList<>();
        for (MemberOtt memberOtt : memberOttList) {
            memberOttDTOList.add(MemberOttDTO.builder().member(memberOtt.getMember()).ottView(memberOtt.getOttView()).build());
        }

        return memberOttDTOList;
    }

    public static List<MemberOtt> toEntityList(Member member, List<OttView> ottViewList) {
        List<MemberOtt> memberOttSet = new ArrayList<>();
        for (OttView ottView : ottViewList) {
            memberOttSet.add(MemberOtt.builder().member(member).ottView(ottView).build());
        }

        // 회원 - 회원OTT 양방향 관계 매핑
        for (MemberOtt memberOtt : memberOttSet) {
            memberOtt.addMember(member);
        }

        return memberOttSet;

    }
}
