package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.ott.service.MemberOttService;
import com.dev.nbbang.member.domain.point.exception.FailCreditRecommendPointException;
import com.dev.nbbang.member.domain.point.exception.NoCreatedPointDetailsException;
import com.dev.nbbang.member.domain.point.service.PointService;
import com.dev.nbbang.member.domain.user.dto.response.MemberAdditionalInformation;
import com.dev.nbbang.member.global.exception.NbbangCommonException;
import com.dev.nbbang.member.global.exception.NbbangException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Slf4j
@RequiredArgsConstructor
public class MemberRegisterConsumer {
    private final String MEMBER_REGISTER_QUEUE = "member.register.queue";

    private final ObjectMapper objectMapper;
    private final PointService pointService;
    private final MemberOttService memberOttService;

    @Transactional
    public void receiverAdditionalInformation(String message) throws JsonProcessingException {
        log.info("[MEMBER REGISTER QUEUE] ReceivedMessage : " + message);


        MemberAdditionalInformation additionalInformation = objectMapper.readValue(message, MemberAdditionalInformation.class);

        // 추천인 아이디가 있는 경우 (Option)
        if (additionalInformation.getMemberId().length() > 0 && additionalInformation.getRecommendMemberId().length() > 0) {
            // 추천인 아이디 적립 시도
            try {
                pointService.updateRecommendPoint(additionalInformation.getMemberId(), additionalInformation.getRecommendMemberId());
            }
            // 해당 회원으로 이미 적립한 이력이 있는 경우는 성공 처리
            catch (FailCreditRecommendPointException e) {
                log.info("이미 처리된 추천인 메세지입니다.");
            }
            // 추천인 적립 이외의 예외로 잡힌 경우
            catch (Exception e) {
                throw new IllegalStateException("추천인 포인트 적립 처리 상태 예외 발생 메세지 재처리 필요");
            }
        }

        // 관심 OTT 정보가 있는 경우 (Option)
        if (additionalInformation.getMemberId().length() > 0 && additionalInformation.getOttId().size() > 0) {
            // 관심 OTT 등록 시도
            try {
                memberOttService.saveMemberOtt(additionalInformation.getMemberId(), additionalInformation.getOttId());
            }
            // 커스텀 예외로 잡히는 경우는 vaildation 실패 재처리 X
            catch (NbbangCommonException e) {
                log.info("Exception Message : {}, 재처리 X", e.getMessage());
            }
            // 커스텀 예외 이외의 경우 재처리 시도
            catch (Exception e)  {
                log.error("Exception Message : {}, 재처리 필요", e.getMessage());

                throw new IllegalStateException("관심 OTT 플랫폼 등록 실패 예외 발생 메세지 재처리 필요");
            }

        }
    }
}
