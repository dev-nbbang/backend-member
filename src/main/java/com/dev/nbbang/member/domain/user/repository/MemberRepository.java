package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    // 가입된 사용자인지 확인
    Optional<Member> findByMemberId(String memberId);
}
