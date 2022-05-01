package com.dev.nbbang.member.domain.point.controller;


import com.dev.nbbang.member.domain.point.dto.PointDTO;
import com.dev.nbbang.member.domain.point.dto.request.MemberPointRequest;
import com.dev.nbbang.member.domain.point.dto.response.MemberPointModifyResponse;
import com.dev.nbbang.member.domain.point.dto.response.MemberPointResponse;
import com.dev.nbbang.member.domain.point.dto.response.PointDetailsResponse;
import com.dev.nbbang.member.domain.point.exception.NoCreatedPointDetailsException;
import com.dev.nbbang.member.domain.point.service.PointService;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.dto.response.CommonFailResponse;
import com.dev.nbbang.member.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/point")
@Slf4j
@Tag(name = "Point", description = "Point API")
public class PointController {
    private final MemberService memberService;
    private final PointService pointService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "포인트 조회", description = "회원의 현재 포인트를 조회한다.")
    public ResponseEntity<?> searchMemberPoint(HttpServletRequest servletRequest) {
        log.info(">> [Nbbang Point Service] 포인트 조회");

        try {
            // 회원 아이디 파싱
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 회원 조회
            MemberDTO findMember = memberService.findMember(memberId);

            return new ResponseEntity<>(MemberPointResponse.create(findMember, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Point Controller - searchMemberPoint] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @PutMapping
    @Operation(summary = "포인트 수정", description = "회원의 현재 포인트를 수정한다.")
    public ResponseEntity<?> changeMemberPoints(@RequestBody MemberPointRequest request, HttpServletRequest servletRequest) {
        log.info(">> [Nbbang Point Service] 포인트 수정");

        try {
            // 회원 아이디 파싱
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 회원 서비스에서 수정 후 포인트 엔티티에 데이터 저장
            PointDTO savePoint = pointService.updatePoint(memberId, MemberPointRequest.toDTO(request));

            return new ResponseEntity<>(MemberPointModifyResponse.create(savePoint, true), HttpStatus.CREATED);

        } catch (NoSuchMemberException | NoCreatedPointDetailsException e) {
            log.info(" >> [Nbbang Point Controller - changeMemberPoints] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/details")
    @Operation(summary = "포인트 상세 이력 조회", description = "회원의 포인트 상세이력을 조회한다.(10개 페이징)")
    public ResponseEntity<?> searchPointDetails(@RequestParam(name = "pointId") Long pointId, @RequestParam(name = "size") int size, HttpServletRequest servletRequest) {
        log.info(" >> [Nbbang Point Service] 포인트 상세 이력 조회");

        try {
            // 회원 아이디 파싱
            String memberId = jwtUtil.getUserid(servletRequest.getHeader("Authorization").substring(7));

            // 포인트 상세이력 조회
            List<PointDTO> findPoint = pointService.findPointDetails(memberId, pointId, size);

            return new ResponseEntity<>(PointDetailsResponse.create(memberId, findPoint, true), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(" >> [Nbbang Point Controller - searchMemberPoint] : " + e.getMessage());

            return new ResponseEntity<>(CommonFailResponse.create(false, e.getMessage()), HttpStatus.OK);
        }
    }
}
