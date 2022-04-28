package com.dev.nbbang.member.domain.point.service;

import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.entity.Point;
import com.dev.nbbang.member.domain.point.exception.NoCreatedPointDetailsException;
import com.dev.nbbang.member.domain.point.repository.PointRepository;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    /**
     * 회원 아이디를 이용해 회원의 현재 포인트를 수정하고, 포인트 사용 타입을 보고 포인트 상세이력을 저장한다.
     * @param memberId JWT 토큰을 파싱한 세션 회원 아이디
     * @param pointDTO  포인트 상세이력을 저장할 포인트  DTO
     * @return PointDTO 포인트 상세이력을 저장한 내용을 반환
     */
    @Override
    @Transactional
    public PointDTO updatePoint(String memberId, PointDTO pointDTO) {
        // 1. 회원 아이디를 이용해 회원 객체 찾기
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        // 2. 찾은 회원 포인트 타입을 보고 수정해주기
        findMember.updatePoint(memberId, pointDTO.getUsePoint(), pointDTO.getPointType());

        // 3. 포인트 상세이력을 저장한다.
        Point savedPoint = Optional.ofNullable(pointRepository.save(PointDTO.toEntity(findMember, pointDTO)))
                .orElseThrow(() -> new NoCreatedPointDetailsException("포인트 상세이력을 저장하는데 실패했습니다.", NbbangException.NO_CREATE_POINT_DETAILS));

        return PointDTO.create(savedPoint);
    }

    /**
     * 회원 아이디를 이용해 회원의 포인트 사용 상세 이력을 10건씩 조회한다.
     * @param memberId JWT 토큰을 파싱한 세션 회원 아이디
     * @return PointDTO 조회환 포인트 상세이력 리스트
     */
    @Override
    public List<PointDTO> findPointDetails(String memberId) {
        return null;
    }
}