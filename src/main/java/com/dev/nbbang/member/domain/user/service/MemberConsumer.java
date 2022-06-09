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
import java.util.Optional;

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

        // 메세지가 중복해서 처리될 수도 있는 경우를 처리하자.
        if(receivedData.getRecommendMemberId().length() > 0) {
            // 포인트의 경우 memberId로 이력에서 조회? 포인트 이력 중복 처리 로직 고민
            pointService.updatePoint(receivedData.getRecommendMemberId());
        }
        if(receivedData.getMemberId().length() > 0 && !receivedData.getOttId().isEmpty()) {
            // 관심 OTT의 경우 중복 처리해도 상관이 없음 (있는 경우 delete 로직을 추가해줄까?)

            memberOttService.saveMemberOtt(receivedData.getMemberId(), receivedData.getOttId());
        }
    }

    @Getter
    @NoArgsConstructor
    static class KafkaReceiveRequest {
        private String memberId;
        private String recommendMemberId;
        private List<Integer> ottId;

        @Builder
        public KafkaReceiveRequest(String memberId, String recommendMemberId, List<Integer> ottId) {
            this.memberId = memberId;
            this.recommendMemberId = recommendMemberId;
            this.ottId = ottId;
        }
    }
}

