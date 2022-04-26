package com.dev.nbbang.member.domain.ott.service;

import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OttViewServiceImpl implements OttViewService{
    private final OttViewRepository ottViewRepository;

    // OTT ID로 OTT 서비스 조회하기
    @Override
    public OttViewDTO findByOttId(Integer ottId) {
        // 1. OTT Id 한개로 조회하기
        OttView ottView = Optional.ofNullable(ottViewRepository.findByOttId(ottId))
                .orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 서비스 입니다.", NbbangException.NOT_FOUND_OTT));
        return OttViewDTO.create(ottView);
    }

    // OTT ID 리스트로 OTT 서비스 조회하기
    @Override
    public List<OttViewDTO> findAllByOttId(List<Integer> ottId) {
        // 1. Ott ID 리스트로 조회하기
        List<OttView> ottView = Optional.ofNullable(ottViewRepository.findAllByOttIdIn(ottId))
                .orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 서비스 입니다.", NbbangException.NOT_FOUND_OTT));
        return OttViewDTO.createList(ottView);
    }

    // 모든 OTT 서비스 조회하기
    @Override
    public List<OttViewDTO> findAll() {
        // 1. 모든 OTT 리스트 조회하기
        List<OttView> findOttView = Optional.of(ottViewRepository.findAll())
                .orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 서비스 입니다.", NbbangException.NOT_FOUND_OTT));

        return OttViewDTO.createList(findOttView);
    }
}
