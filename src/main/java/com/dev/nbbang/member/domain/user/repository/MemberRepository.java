package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // 아이디로 회원 찾기
    Member findByMemberId(String memberId);

    // 닉네임으로 회원 찾기
    Member findByNickname(String nickname);

    // 비슷한 닉네임 5개 가져오기
    List<Member> findTop5ByNicknameStartingWith(String nickname);

    // 회원 삭제하기
    void deleteByMemberId(String memberId);

    // 회원 저장하기
    Member save(Member member);

}

