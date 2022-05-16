package com.dev.nbbang.member.domain.point.dto.response;

import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MemberPointModifyResponse {
    private String memberId;
    private Long point;

    private LocalDateTime pointYmd;

    public static MemberPointModifyResponse create(PointDTO pointDetails) {
        return MemberPointModifyResponse.builder()
                .memberId(pointDetails.getMember().getMemberId())
                .point(pointDetails.getMember().getPoint())
                .build();
    }
}
