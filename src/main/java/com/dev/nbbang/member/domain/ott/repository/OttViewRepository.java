package com.dev.nbbang.member.domain.ott.repository;

import com.dev.nbbang.member.domain.ott.entity.OttView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OttViewRepository extends JpaRepository<OttView, Integer> {
    // OTT 아이디를 통해 OTT 서비스 찾기
    OttView findByOttId(int ottId);

    // OTT 아이디 리스트를 통해 한번에 찾기
    List<OttView> findAllByOttIdIn(List<Integer> ottId);

    // 엔빵에서 제공하는 OTT 서비스 조회 시 findAll() 사용
}
