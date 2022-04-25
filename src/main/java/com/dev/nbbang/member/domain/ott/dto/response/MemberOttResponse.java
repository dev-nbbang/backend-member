package com.dev.nbbang.member.domain.ott.dto.response;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberOttResponse {
    private String memberId;
    private OttView ottView;
    private boolean status;

    /**
     * 관심 OTT 등록 후 회원 아이디와 관심 OTT 내용이 들어간 리스트를 리턴
     * @param memberOttDTOList "관심 OTT 등록 후 리턴한 MemberOttDto 타입 리스트"
     * @param status "상태값"
     * @return savedMemberOtt "MemberOttResponse 타입 리스트"
     */
    public static List<MemberOttResponse> createList(List<MemberOttDTO> memberOttDTOList, boolean status) {
        List<MemberOttResponse> memberOtt  = new ArrayList<>();

        for (MemberOttDTO memberOttDTO : memberOttDTOList) {
            memberOtt.add(MemberOttResponse.builder()
                    .memberId(memberOttDTO.getMember().getMemberId())
                    .ottView(memberOttDTO.getOttView())
                    .status(status).build());
        }

        return memberOtt;
    }
}
