package com.dev.nbbang.member.domain.point.service;

import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PointService {
    // 포인트 수정
    PointDTO updatePoint(String memberId, PointDTO pointDTO);

    // 추천인 회원 적립
    PointDTO updateRecommendPoint(String memberId, String recommendMemberId);

    // 포인트 이력 조회
    List<PointDTO> findPointDetails(String memberId, Long pointId, int size);
}
