package com.dev.nbbang.member.domain.point.repository;

import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PointRepositoryTest {
    @Autowired
    private PointRepository pointRepository;

    @Test
    @DisplayName("포인트 레포지토리 : 포인트 상세이력 불러오기 성공")
    void 포인트_상세이력_불러오기_성공() {
        // given
        Member findMember = testMember();
        Pageable pageable = PageRequest.of(0, 3);

        // when
        Page<Point> findPointDetail = pointRepository.findByIdLessThanAndMemberOrderByIdDesc(22L, findMember, pageable);

        // then
        assertEquals(findPointDetail.getSize(),3);
        assertEquals(findPointDetail.getContent().get(0).getId(), 21);
        assertEquals(findPointDetail.getContent().get(1).getId(), 20);
        assertEquals(findPointDetail.getContent().get(2).getId(), 19);
    }

    @Test
    @DisplayName("포인트 레포지토리 : 포인트 상세이력 불러오기 실패")
    void 포인트_상세이력_불러오기_실패() {
        // given
        Member findMember = Member.builder().memberId("fou").build();
        Pageable pageable = PageRequest.of(0, 3);

        // when
        Page<Point> findPointDetail = pointRepository.findByIdLessThanAndMemberOrderByIdDesc(1L, findMember, pageable);

        // then
        assertEquals(findPointDetail.getTotalPages(), 0);
        org.assertj.core.api.Assertions.assertThat(findPointDetail).isEmpty();
    }

    @Test
    @DisplayName("포인트 레포지토리 : 포인트 상세이력 저장 성공")
    void 포인트_상세이력_저장_성공() {
        //given
        Point point = testPoint();

        // when
        Point savePointDetail = pointRepository.save(point);

        //then
        assertEquals(savePointDetail.getPointType(), PointType.INCREASE);
        assertEquals(savePointDetail.getUsePoint(), testPoint().getUsePoint());
    }

    @Test
    @DisplayName("포인트 레포지토리 : 포인트 상세이력 저장 실패")
    void 포인트_상세이력_저장_실패() {
        //given
        Point point = Point.builder().member(Member.builder().memberId("NO MEMBER").point(0L).build()).usePoint(100L).build();

        //when
        assertThrows(DataIntegrityViolationException.class, () -> pointRepository.save(point));
    }

    private static Member testMember() {
        return Member.builder()
                .memberId("K-2197723261")
//                .memberId("test")
                .point(1000L)
                .build();
    }

    private static Point testPoint() {
        return Point.builder()
                .member(testMember())
                .pointType(PointType.INCREASE)
                .pointDetail("본전도 못찾았네")
                .usePoint(500L).build();
    }
}