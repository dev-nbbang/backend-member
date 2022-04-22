package com.dev.nbbang.member.domain.ott.service;

import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OttViewServiceImpl implements OttViewService{
    private final OttViewRepository ottViewRepository;

    @Override
    public OttViewDTO findByOttId(int ottId) {
        OttView ottView = ottViewRepository.findByOttId(ottId).orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 서비스 입니다.", NbbangException.NOT_FOUND_OTT));
        return OttViewDTO.create(ottView);
    }

    // OTT ID로 OTT 불러오기
    @Override
    public List<OttViewDTO> findAllByOttId(List<Integer> ottId) {
        List<OttView> ottView = ottViewRepository.findAllByOttIdIn(ottId).orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 서비스 입니다.", NbbangException.NOT_FOUND_OTT));
        return OttViewDTO.createList(ottView);
    }
}
