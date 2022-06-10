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
            // 추천인 적립을 서비스에서 중복 적립 검증 했으므로 메세지의 중복 읽기에 대해 동일한 결과를 반환 (멱등성 보장)
            pointService.updateRecommendPoint(receivedData.getMemberId(), receivedData.getRecommendMemberId());
        }
        if(receivedData.getMemberId().length() > 0 && !receivedData.getOttId().isEmpty()) {
            // 관심 OTT 등록은 기존의 관심 OTT 삭제 후 추가 형식이므로 메세지 중복 처리에 대해 동일한 결과 반환 (멱등성 보장)
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

