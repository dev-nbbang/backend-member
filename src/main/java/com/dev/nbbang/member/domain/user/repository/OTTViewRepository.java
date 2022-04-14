package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.OTTView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTTViewRepository extends JpaRepository<OTTView, Integer> {
    OTTView findByOttId(int ottId);
}
