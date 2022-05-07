package com.dev.nbbang.member.domain.point.repository;

import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    // 데이터 불러오기 페이징 처리로 구현 (10개)
    Page<Point> findByIdLessThanAndMemberOrderByIdDesc(Long id, Member member, Pageable pageable);

    // 페이징 처리 타입에 맞게 사용
    Slice<Point> findByIdGreaterThanAndMemberOrderById(Long id, Member member, Pageable pageable);

    // 포인트 이력 추가
    Point save(Point point);
}
