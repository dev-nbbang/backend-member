package com.dev.nbbang.member.domain.point.dto.response;

import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MemberPointModifyResponse {
    private String memberId;
    private Long usePoint;
    private String pointDetail;
    private PointType pointType;
    private boolean status;

    public static MemberPointModifyResponse create(MemberDTO member) {
        return MemberPointModifyResponse.builder()
                .memberId(member.getMemberId()).build();
//                .usePoint(member.getPointList().)
    }
}
