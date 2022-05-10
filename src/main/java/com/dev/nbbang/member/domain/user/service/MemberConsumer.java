package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.ott.service.MemberOttService;
import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.dto.request.MemberPointRequest;
import com.dev.nbbang.member.domain.point.service.PointService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberConsumer {
    private final MemberOttService memberOttService;
    private final PointService pointService;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "new-member-register")
    public void saveRecommenderPointAndMemberOtt(String receivedMessage) throws JsonProcessingException {
        log.info("[MemberConsumer] ReceivedMessage : " + receivedMessage);

        KafkaReceiveRequest receivedData = objectMapper.readValue(receivedMessage, KafkaReceiveRequest.class);

        if(!receivedData.getRecommendId().isEmpty()) {
            pointService.updatePoint(receivedData.getRecommendId());
        }
        if(!receivedData.getMemberId().isEmpty() && !receivedData.getOttId().isEmpty()) {
            memberOttService.saveMemberOtt(receivedData.getMemberId(), receivedData.getOttId());
        }
    }

    @Getter
    @NoArgsConstructor
    static class KafkaReceiveRequest {
        private String memberId;
        private String recommendId;
        private List<Integer> ottId;

        @Builder
        public KafkaReceiveRequest(String memberId, String recommendId, List<Integer> ottId) {
            this.memberId = memberId;
            this.recommendId = recommendId;
            this.ottId = ottId;
        }
    }
}
