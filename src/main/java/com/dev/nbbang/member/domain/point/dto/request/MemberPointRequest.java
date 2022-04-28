package com.dev.nbbang.member.domain.point.dto.request;

import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.entity.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MemberPointRequest {
    private String memberId;
    private Long usePoint;
    private String pointDetail;
    private PointType pointType;

    public static PointDTO toDTO(MemberPointRequest request) {
        return PointDTO.builder()
                .usePoint(request.getUsePoint())
                .pointDetail(request.getPointDetail())
                .pointType(request.getPointType())
                .build();
    }
}
