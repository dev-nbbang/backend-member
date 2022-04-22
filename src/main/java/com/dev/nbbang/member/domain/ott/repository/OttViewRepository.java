package com.dev.nbbang.member.domain.ott.repository;

import com.dev.nbbang.member.domain.ott.entity.OttView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OttViewRepository extends JpaRepository<OttView, Integer> {
    Optional<OttView> findByOttId(int ottId);

    Optional<List<OttView>> findAllByOttIdIn(List<Integer> ottId);

}
