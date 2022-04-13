package com.dev.nbbang.member.domain.user.repository;

import com.dev.nbbang.member.domain.user.entity.OTTView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTTViewRepository extends JpaRepository<OTTView, Integer> {
    OTTView findByOttId(int ottId);
}
