package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // 아이디로 회원 찾기
    Optional<Member> findByMemberId(String memberId);

    // 닉네임으로 회원 찾기
    Optional<Member> findByNickname(String nickname);

    // 비슷한 닉네임 5개 가져오기
    Optional<List<Member>> findTop5ByNicknameStartingWith(String nickname);

    // 회원 삭제하기
    void deleteByMemberId(String memberId);

    // 회원 저장하기
    Member save(Member member);

    // 테스트 회원 찾기 테스트 매핑 (JPQL)
//    @Query("select m from Member as m INNER JOIN F")
}

