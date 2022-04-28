package com.dev.nbbang.member.domain.point.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberPointResponse {
    private String memberId;
    private Long point;
    private boolean status;

    public static MemberPointResponse create(MemberDTO member, boolean status) {
        return MemberPointResponse.builder()
                .memberId(member.getMemberId())
                .point(member.getPoint())
                .status(status).build();
    }
}
