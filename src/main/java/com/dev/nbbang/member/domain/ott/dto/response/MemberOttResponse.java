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
    private List<OttView> ottView;
    private boolean status;

    /**
     * @apiNote 관심 OTT 등록 후 회원 아이디와 관심 OTT 내용이 들어간 리스트를 리턴
     * @param memberOttDTOList "관심 OTT 등록 후 리턴한 MemberOttDto 타입 리스트"
     * @param status "상태값"
     * @return savedMemberOtt "MemberOttResponse 타입 리스트"
     */

    public static MemberOttResponse create(List<MemberOttDTO> memberOttDTOList, boolean status) {
        List<OttView> ottView = new ArrayList<>();
        for (MemberOttDTO memberOttDTO : memberOttDTOList) {
            ottView.add(memberOttDTO.getOttView());
        }

        return MemberOttResponse.builder().memberId(memberOttDTOList.get(0).getMember().getMemberId()).ottView(ottView).status(status).build();
    }
}
