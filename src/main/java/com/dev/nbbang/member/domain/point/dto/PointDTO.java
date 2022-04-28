package com.dev.nbbang.member.domain.point.dto;

import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PointDTO {
    private Member member;
    private Long usePoint;
    private String pointDetail;
    private PointType pointType;
    private LocalDateTime pointYmd;

    public static PointDTO create(Point point) {
        return PointDTO.builder()
                .member(point.getMember())
                .usePoint(point.getUsePoint())
                .pointDetail(point.getPointDetail())
                .pointType(point.getPointType())
                .pointYmd(point.getPointYmd()).build();
    }

    public static Point toEntity(Member member, PointDTO pointDTO) {
        return Point.builder()
                .member(member)
                .usePoint(pointDTO.getUsePoint())
                .pointDetail(pointDTO.getPointDetail())
                .pointType(pointDTO.getPointType())
                .build();
    }
}
