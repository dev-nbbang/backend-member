package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.dto.request.MemberLeaveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendLeaveMemberMessage(MemberLeaveRequest request) throws JsonProcessingException {
        // 회원 탈퇴 이벤트 전송 시작
        log.info("[Leave Member Message Send] : 회원 탈퇴 이벤트 전송 (Member Service -> Alarm Service, Party Service)");
        final String MEMBER_LEAVE_TOPIC = "leave-member";

        // 회원 아이디 Request 생성
        String sendMessage = objectMapper.writeValueAsString(request);
        log.info("[Leave Member Message Send] :  message : " + sendMessage);

        // 회원 탈퇴 이벤트 카프카 전송
        kafkaTemplate.send(MEMBER_LEAVE_TOPIC, sendMessage);
    }


}
