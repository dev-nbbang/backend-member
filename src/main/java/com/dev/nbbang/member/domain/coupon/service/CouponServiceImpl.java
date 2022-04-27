package com.dev.nbbang.member.domain.coupon.service;

import com.dev.nbbang.member.domain.coupon.entity.Coupon;
import com.dev.nbbang.member.domain.coupon.entity.MemberCoupon;
import com.dev.nbbang.member.domain.coupon.exception.AlreadyUsedCouponException;
import com.dev.nbbang.member.domain.coupon.exception.DuplicationCouponException;
import com.dev.nbbang.member.domain.coupon.exception.NoSuchCouponException;
import com.dev.nbbang.member.domain.coupon.repository.CouponRepository;
import com.dev.nbbang.member.domain.coupon.repository.MemberCouponRepository;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;

    // 사용자 쿠폰 리스트 조회
    @Override
    public List<MemberCoupon> memberCouponList(String memberId) {
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 없습니다", NbbangException.NOT_FOUND_MEMBER));
        return memberCouponRepository.findAllByMemberAndUseYN(member, "N").orElseThrow(
                () -> new NoSuchCouponException("해당 회원이 가지고 있는 쿠폰이 없습니다.", NbbangException.NOT_FOUND_COUPON));
    }

    // 사용자 쿠폰 저장
    @Override
    @Transactional
    public void saveMemberCoupon(String memberId, int couponId) {
        Coupon coupon = couponRepository.findByCouponId(couponId).orElseThrow(() -> new NoSuchCouponException("쿠폰이 없습니다", NbbangException.NOT_FOUND_COUPON));
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 없습니다", NbbangException.NOT_FOUND_MEMBER));
        Optional<MemberCoupon> memberCoupon = memberCouponRepository.findByMemberAndCoupon(member, coupon);
        if(memberCoupon.isPresent()) throw new DuplicationCouponException("쿠폰이 이미 존재합니다", NbbangException.Duplication_Coupon);
        LocalDate now = LocalDate.now();
        Date nowDate = Date.valueOf(now);
        now = now.plusDays(coupon.getExpireDay());
        Date expireDate = Date.valueOf(now);
        memberCouponRepository.save(MemberCoupon.builder().member(member).coupon(coupon).useYN("N").startYmd(nowDate).expireYmd(expireDate).build());
    }

    // 사용자 쿠폰 사용 처리
    @Override
    @Transactional
    public void updateMemberCoupon(String memberId, int couponId) {
        Coupon coupon = couponRepository.findByCouponId(couponId).orElseThrow(()-> new NoSuchCouponException("해당 되는 쿠폰이 없습니다.", NbbangException.NOT_FOUND_COUPON));
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 없습니다", NbbangException.NOT_FOUND_MEMBER));
        MemberCoupon memberCoupon = memberCouponRepository.findByMemberAndCoupon(member, coupon).orElseThrow(() -> new NoSuchCouponException("해당 회원이 가지고 있는 쿠폰이 없습니다.", NbbangException.NOT_FOUND_COUPON));
        if(memberCoupon.getUseYN() == "Y") throw new AlreadyUsedCouponException("이미 사용된 쿠폰입니다", NbbangException.Already_Used_Coupon);
        memberCoupon.updateMemberCoupon(memberCoupon.getMember(), "Y");
    }

    // 사용자 쿠폰 삭제
    @Override
    @Transactional
    public void deleteMemberCoupon(String memberId, int couponId) {
        Coupon coupon = couponRepository.findByCouponId(couponId).orElseThrow(()-> new NoSuchCouponException("해당 되는 쿠폰이 없습니다.", NbbangException.NOT_FOUND_COUPON));
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("해당 회원이 없습니다", NbbangException.NOT_FOUND_MEMBER));
        memberCouponRepository.deleteByMemberAndCoupon(member, coupon);
    }


    //관리자 영역
    //쿠폰 전체 조회
    @Override
    public List<Coupon> findCouponList() {
        List<Coupon> couponList = couponRepository.findAll();
        if(couponList.size() == 0) throw new NoSuchCouponException("쿠폰리스트가 존재하지 않습니다", NbbangException.NOT_FOUND_COUPON);
        return couponList;
    }
    //쿠폰 조회
    @Override
    public Coupon findCoupon(int couponId) {
        return couponRepository.findByCouponId(couponId).orElseThrow(()-> new NoSuchCouponException("쿠폰이 존재하지 않습니다", NbbangException.NOT_FOUND_COUPON));
    }
    //쿠폰 등록,수정
    @Override
    @Transactional
    public void save(Coupon coupon) {
        couponRepository.save(coupon);
    }

    //쿠폰 삭제
    @Override
    @Transactional
    public void deleteCoupon(int couponId) {
        couponRepository.deleteByCouponId(couponId);
    }
}
