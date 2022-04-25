package com.dev.nbbang.member.domain.ott.service;

import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import com.dev.nbbang.member.domain.ott.entity.OttView;

import java.util.List;

public interface OttViewService  {

    OttViewDTO findByOttId(int ottId);

    List<OttViewDTO> findAllByOttId(List<Integer> ottId);

    List<OttViewDTO> findAll();
}
