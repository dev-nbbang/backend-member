package com.dev.nbbang.member.domain.point.dto.response;

import com.dev.nbbang.member.domain.point.dto.PointDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PointDetailsResponse {
    private String memberId;
    private List<PointDTO> pointDetails;
    private boolean status;
    private String message;

    public static PointDetailsResponse create(String memberId, List<PointDTO> points, boolean status, String message) {
        return PointDetailsResponse.builder()
                .memberId(memberId)
                .pointDetails(customDetail(points))
                .status(status)
                .message(message)
                .build();
    }

    private static List<PointDTO> customDetail(List<PointDTO> points) {
        List<PointDTO> customDetails = new ArrayList<>();
        for (PointDTO point : points) {
            customDetails.add(PointDTO.builder()
                    .pointId(point.getPointId())
                    .usePoint(point.getUsePoint())
                    .pointDetail(point.getPointDetail())
                    .pointType(point.getPointType())
                    .pointYmd(point.getPointYmd())
                    .build());
        }
        return customDetails;
    }
}
