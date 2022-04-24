package com.dev.nbbang.member.domain.ott.repository;

import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberOttRepository extends JpaRepository<MemberOtt, Long> {
    // 관심 OTT 저장 saveAll 사용

    // 관심 OTT 불러오기
    Optional<List<MemberOtt>> findAllByMember(Member member);

    // 관심 OTT 전체 삭제
    void deleteByMember(Member member);

    // 관심 OTT 삭제
    void deleteByMemberAndOttView(Member member, OttView ottView);
}
