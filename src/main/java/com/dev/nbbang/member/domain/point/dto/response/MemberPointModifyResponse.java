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
    private Long usePoint;
    private String pointDetail;
    private PointType pointType;
    private LocalDateTime pointYmd;
    private boolean status;
    private String message;

    public static MemberPointModifyResponse create(PointDTO pointDetails, boolean status, String message) {
        return MemberPointModifyResponse.builder()
                .memberId(pointDetails.getMember().getMemberId())
                .usePoint(pointDetails.getUsePoint())
                .pointDetail(pointDetails.getPointDetail())
                .pointType(pointDetails.getPointType())
                .pointYmd(pointDetails.getPointYmd())
                .status(status)
                .message(message)
                .build();
    }
}
