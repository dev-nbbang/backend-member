package com.dev.nbbang.member.domain.memberott.repository;

import com.dev.nbbang.member.domain.memberott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberOttRepository extends JpaRepository<MemberOtt, Long> {
    // 관심 OTT 저장
    MemberOtt save(MemberOtt memberOtt);

//    @Override
//    <S extends MemberOtt> List<S> saveAll(Iterable<S> entities);

//    List<MemberOtt> saveAll(List<MemberOtt> memberOttList);
    // 관심 OTT 삭제
    void deleteByMember(Member member);

}
