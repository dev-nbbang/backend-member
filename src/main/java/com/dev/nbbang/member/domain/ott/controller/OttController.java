package com.dev.nbbang.member.domain.ott.controller;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import com.dev.nbbang.member.domain.ott.dto.request.MemberOttRequest;
import com.dev.nbbang.member.domain.ott.dto.response.MemberOttResponse;
import com.dev.nbbang.member.domain.ott.dto.response.OttViewResponse;
import com.dev.nbbang.member.domain.ott.exception.FailDeleteMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchMemberOttException;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.ott.service.MemberOttService;
import com.dev.nbbang.member.domain.ott.service.OttViewService;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.global.dto.response.CommonResponse;
import com.dev.nbbang.member.global.dto.response.CommonSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/ott-interest")
@Tag(name = "OTT", description = "OTT API")
public class OttController {
    private final OttViewService ottViewService;
    private final MemberOttService memberOttService;

    @PostMapping(value = "/new")
    @Operation(summary = "관심 OTT 등록", description = "회원 아이디와 OTT ID를 받아 관심 OTT로 등록한다.")
    public ResponseEntity<?> registerMemberOtt(@RequestBody MemberOttRequest request, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Ott Service] 관심 OTT 등록");

        // 인증헤더에서 아이디 가져오기
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 관심 OTT 등록
        List<MemberOttDTO> savedMemberOtt = memberOttService.saveMemberOtt(memberId, request.getOttId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonSuccessResponse.response(true, MemberOttResponse.create(savedMemberOtt), "관심 OTT 서비스 등록에 성공했습니다."));
    }

    @GetMapping
    @Operation(summary = "관심 OTT 조회", description = "회원 아이디를 이용해 관심 OTT를 조회한다.")
    public ResponseEntity<?> searchMemberOtt(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Ott Service] 관심 OTT 조회");

        // 인증헤더에서 아이디 가져오기
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 관심 OTT 조회
        List<MemberOttDTO> findMemberOtt = memberOttService.findMemberOttByMemberId(memberId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, MemberOttResponse.create(findMemberOtt), "관심 OTT 서비스 조회에 성공했습니다."));
    }

    @DeleteMapping(value = "/{ottId}")
    @Operation(summary = "관심 OTT 한 개 삭제", description = "회원 아이디와 OTT Id를 받아 등록된 관심 OTT 한개를 삭제한다")
    public ResponseEntity<?> deleteMemberOtt(@PathVariable(name = "ottId") Integer ottId, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Ott Service] 관심 OTT 한개 삭제");

        // 인증헤더에서 아이디 가져오기
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 관심 OTT 한개 삭제
        memberOttService.deleteMemberOtt(memberId, ottId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @Operation(summary = "관심 OTT 전체 삭제", description = "회원 아이디를 받아 등록된 관심 OTT를 전체 삭제한다")
    public ResponseEntity<?> deleteAllMemberOtt(HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Ott Service] 관심 OTT 한개 삭제");

        // 인증헤더에서 아이디 가져오기
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 관심 OTT 한개 삭제
        memberOttService.deleteAllMemberOtt(memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/list")
    @Operation(summary = "모든 OTT 서비스 조회", description = "엔빵에 등록된 모든 OTT 서비스 조회하기")
    public ResponseEntity<?> searchAllOttView() {
        log.info(" >> [Nbbang Ott Service] 모든 OTT 서비스 조회");

        // 모든 OTT 서비스 조회
        List<OttViewDTO> findOttView = ottViewService.findAll();

        // response 타입으로 빼기
        return ResponseEntity.ok(CommonSuccessResponse.response(true, OttViewResponse.create(findOttView), "등록된 모든 OTT 서비스 상세정보 조회에 성공했습니다."));
    }
}

