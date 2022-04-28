package com.dev.nbbang.member.domain.point.repository;

import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    // 페이징 처리로 구현 (10개)
    Page<Point> findByIdLessThanAndMemberOrderByIdDesc(Long id, Member member, Pageable pageRequest);

    // 포인트 이력 추가
    Point save(Point point);
}
