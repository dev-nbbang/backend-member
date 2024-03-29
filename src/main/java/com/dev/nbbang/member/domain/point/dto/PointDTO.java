package com.dev.nbbang.member.domain.point.dto;

import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PointDTO {
    private Member member;
    private Long pointId;
    private Long usePoint;
    private String pointDetail;
    private PointType pointType;
    private LocalDateTime pointYmd;
    private String nomineeId;

    public static PointDTO create(Point point) {
        return PointDTO.builder()
                .member(point.getMember())
                .pointId(point.getId())
                .usePoint(point.getUsePoint())
                .pointDetail(point.getPointDetail())
                .pointType(point.getPointType())
                .pointYmd(point.getPointYmd()).build();
    }

    public static List<PointDTO> createList(Member member, Page<Point> points) {
        List<PointDTO> pointDTOS = new ArrayList<>();
        for (Point point : points) {
            pointDTOS.add(PointDTO.builder()
                    .member(member)
                    .pointId(point.getId())
                    .usePoint(point.getUsePoint())
                    .pointDetail(point.getPointDetail())
                    .pointType(point.getPointType())
                    .pointYmd(point.getPointYmd())
                    .build());
        }

        return pointDTOS;
    }


    public static Point toEntity(Member member, PointDTO pointDTO) {
        return Point.builder()
                .member(member)
                .usePoint(pointDTO.getUsePoint())
                .pointDetail(pointDTO.getPointDetail())
                .pointType(pointDTO.getPointType())
                .pointYmd(LocalDateTime.now())
                .build();
    }

    // 추천인 적립
    public static Point toEntity(String nomineeId, Member member) {
        return Point.builder()
                .member(member)
                .usePoint(500L)
                .pointDetail(nomineeId + "님의 추천인 적립!")
                .pointType(PointType.INCREASE)
                .pointYmd(LocalDateTime.now())
                .nomineeId(nomineeId)
                .build();
    }
}
