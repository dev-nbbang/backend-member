package com.dev.nbbang.member.domain.point.repository;

import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    // 페이징 처리로 구현 (10개)
    Point findPointByMember(Member member);

    // 포인트 이력 추가
    Point save(Point point);
}
