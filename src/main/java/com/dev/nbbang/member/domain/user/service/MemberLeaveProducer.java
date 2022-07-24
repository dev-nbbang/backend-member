package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.dto.request.MemberLeaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberLeaveProducer {
    private final RabbitTemplate rabbitTemplate;

    private final String NBBANG_EXCHANGE = "nbbang.exchange";
    private final String MEMBER_LEAVE_ROUTING_KEY = "member.leave.route";

    public void sendLeaveMemberMessage(MemberLeaveRequest request) {
        // 회원 탈퇴 이벤트 전송 시작
        log.info("[MEMBER LEAVE QUEUE] : 회원 탈퇴 이벤트 전송 (Member Service -> Alarm Service, Party Service)");

        // 회원 아이디 Request 생성
        log.debug("[MEMBER LEAVE QUEUE] MESSAGE : {}", request.toString());

        // 회원 탈퇴 이벤트 카프카 전송
        rabbitTemplate.convertAndSend(NBBANG_EXCHANGE, MEMBER_LEAVE_ROUTING_KEY, request);
    }
}
