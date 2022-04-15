package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByNickname(String nickname);
    Optional<List<Member>> findTop5ByNicknameStartingWith(String nickname);
    Member save(Member member);

}
