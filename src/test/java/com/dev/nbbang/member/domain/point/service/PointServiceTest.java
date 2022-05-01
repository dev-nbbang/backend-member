package com.dev.nbbang.member.domain.point.service;

import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.entity.PointType;
import com.dev.nbbang.member.domain.point.exception.NoCreatedPointDetailsException;
import com.dev.nbbang.member.domain.point.repository.PointRepository;
import com.dev.nbbang.member.domain.user.entity.Grade;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {
    @Mock
    private PointRepository pointRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    @Test
    @DisplayName("포인트 서비스 : 포인트 상세이력 저장 , 포인트 추가 성공")
    void 포인트_상세이력_저장_및_포인트_추가_성공() {
        /**
         * 1. 회원 아이디로 회원 찾기
         * 2. 포인트 적립 로직
         */
        // given
        String memberId = "testId";
        Member findMember = testMember();
        Point increasePoint = testIncreasePoint();
        given(memberRepository.findByMemberId(anyString())).willReturn(findMember);
        given(pointRepository.save(any())).willReturn(increasePoint);

        // when
        PointDTO savedPoint = pointService.updatePoint(memberId, testIncreasePointDTO());

        // then

        assertThat(savedPoint.getMember().getPoint()).isEqualTo(findMember.getPoint());
    }

    @Test
    @DisplayName("포인트 서비스 : 포인트 상세이력 저장 , 포인트 추가 실패")
    void 포인트_상세이력_저장_및_포인트_추가_실패() {
        /**
         * 1. 회원 아이디로 회원 찾기
         * 2. 포인트 적립 실패 (예외처리) -> NoCreatedPointDetailsException
         */
        // given
        String memberId = "testId";
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(pointRepository.save(any())).willThrow(NoCreatedPointDetailsException.class);

        // then
        assertThrows(NoCreatedPointDetailsException.class, () -> pointService.updatePoint(memberId, testIncreasePointDTO()));
    }

    @Test
    @DisplayName("포인트 서비스 : 포인트 상세이력 저장 , 포인트 감소 성공")
    void 포인트_상세이력_저장_및_포인트_감소_성공() {
        /**
         * 1. 회원 아이디로 회원 찾기
         * 2. 포인트 사용 로직
         */
        // given
        String memberId = "testId";
        Member findMember = testMember();
        Point decreasePoint = testDecreasePoint();
        given(memberRepository.findByMemberId(anyString())).willReturn(findMember);
        given(pointRepository.save(any())).willReturn(decreasePoint);

        // when
        PointDTO savedPoint = pointService.updatePoint(memberId, testDecreasePointDTO());

        // then
        assertThat(savedPoint.getMember().getPoint()).isEqualTo(findMember.getPoint());
    }

    @Test
    @DisplayName("포인트 서비스 : 포인트 상세이력 저장 , 포인트 감소 실패")
    void 포인트_상세이력_저장_및_포인트_감소_실패() {
        /**
         * 1. 회원 아이디로 회원 찾기
         * 2. 포인트 사용 실패 (예외처리) -> NoCreatedPointDetailsException
         */
        // given
        String memberId = "testId";
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(pointRepository.save(any())).willThrow(NoCreatedPointDetailsException.class);

        // then
        assertThrows(NoCreatedPointDetailsException.class, () -> pointService.updatePoint(memberId, testDecreasePointDTO()));
    }

    @Test
    @DisplayName("포인트 서비스 : 포인트 상세이력 조회하기 성공")
    void 포인트_상세이력_조회하기_성공() {
        /**
         * 1. 회원 아이디로 회원 찾기
         * 2. 찾은 회원을 통해 이력 조회하기
         */
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());
        given(pointRepository.findByIdLessThanAndMemberOrderByIdDesc(anyLong(), any(), any())).willReturn(testPagePoint());

        // when
        List<PointDTO> findPoint = pointService.findPointDetails("testId", 2L, 3);

        // then
        assertThat(findPoint.size()).isEqualTo(2);
        assertThat(findPoint.get(0).getMember().getMemberId()).isEqualTo("testId");
        assertThat(findPoint.get(0).getPointType()).isEqualTo(PointType.DECREASE);
    }

    @Test
    @DisplayName("포인트 서비스 : 포인트 상세이력 조회하기 실패")
    void 포인트_상세이력_조회하기_실패() {
        /**
         * 1. 회원 아이디로 회원 찾기 (예외발생) -> NoSuchMemberException
         */
        String memberId = "testId";
        given(memberRepository.findByMemberId(anyString())).willThrow(NoSuchMemberException.class);

        // then
        assertThrows(NoSuchMemberException.class, () -> pointService.findPointDetails(memberId, 1L, 0));
    }

    private static Member testMember() {
        return Member.builder()
                .memberId("testId")
                .nickname("testNickname")
                .point(1000L)
                .grade(Grade.BRONZE)
                .exp(0L)
                .partyInviteYn("Y")
                .build();
    }

    private static Member testIncreaseMember() {
        return Member.builder()
                .memberId("testId")
                .nickname("testNickname")
                .point(1500L)
                .grade(Grade.BRONZE)
                .exp(0L)
                .partyInviteYn("Y")
                .build();
    }

    private static Member testDecreaseMember() {
        return Member.builder()
                .memberId("testId")
                .nickname("testNickname")
                .point(500L)
                .grade(Grade.BRONZE)
                .exp(0L)
                .partyInviteYn("Y")
                .build();
    }

    private static Point testIncreasePoint() {
        return Point.builder()
                .id(1L)
                .member(testIncreaseMember())
                .usePoint(500L)
                .pointDetail("테스트 포인트 적립")
                .pointType(PointType.INCREASE)
                .build();
    }

    private static Point testDecreasePoint() {
        return Point.builder()
                .id(2L)
                .member(testDecreaseMember())
                .usePoint(500L)
                .pointType(PointType.DECREASE)
                .pointDetail("테스트 포인트 사용")
                .build();
    }

    private static PointDTO testIncreasePointDTO() {
        return PointDTO.builder()
                .member(testMember())
                .usePoint(500L)
                .pointDetail("테스트 포인트 적립")
                .pointType(PointType.INCREASE)
                .build();
    }

    private static PointDTO testDecreasePointDTO() {
        return PointDTO.builder()
                .member(testMember())
                .usePoint(500L)
                .pointType(PointType.DECREASE)
                .pointDetail("테스트 포인트 사용")
                .build();
    }

    private static Page<Point> testPagePoint() {
        return new PageImpl<>(new ArrayList<>(Arrays.asList(testDecreasePoint(), testIncreasePoint())));
    }
}