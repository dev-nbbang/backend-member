package com.dev.nbbang.member.domain.account.controller;

import com.dev.nbbang.member.domain.account.dto.request.AccountRequest;
import com.dev.nbbang.member.domain.account.entity.Bank;
import com.dev.nbbang.member.domain.account.service.AccountService;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.account.api.service.ImportAPI;
import com.dev.nbbang.member.domain.account.dto.request.CardRequest;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {
    private final ImportAPI importAPI;
    private final MemberService memberService;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @GetMapping("/")
    @Operation(description = "계좌 조회")
    public ResponseEntity<?> accountCheck(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        System.out.println(memberId);
        Map<String, Object> result = new HashMap<>();
        try {
            MemberDTO member = memberService.findMember(memberId);
            String bankAccount = accountService.decrypt(member.getBankAccount());
            result.put("bankAccount", bankAccount);
            result.put("status", true);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            log.info(e.getMessage());
            log.info("회원정보 없음");
            result.put("status", false);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/new")
    @Operation(description = "계좌 등록")
    public ResponseEntity<?> accountRegister(@RequestBody AccountRequest accountRequest, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        Map<String, Object> result = new HashMap<>();
        try {
            String bankAccountEnc = accountService.encrypt(accountRequest.getBankAccount());
            accountRequest.encBankAccount(bankAccountEnc);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        memberService.updateAccount(memberId, AccountRequest.toEntity(accountRequest));
        result.put("status", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/")
    @Operation(description = "계좌 수정")
    public ResponseEntity<?> accountUpdate(@RequestBody AccountRequest accountRequest, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        Map<String, Object> result = new HashMap<>();
        try {
            String bankAccountEnc = accountService.encrypt(accountRequest.getBankAccount());
            accountRequest.encBankAccount(bankAccountEnc);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        memberService.updateAccount(memberId, AccountRequest.toEntity(accountRequest));
        result.put("status", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/")
    @Operation(description = "계좌 삭제")
    public ResponseEntity<?> accountDelete(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        Map<String, Object> result = new HashMap<>();
        memberService.deleteAccount(memberId);
        result.put("status", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/billing")
    @Operation(description = "빌링키 조회")
    public ResponseEntity<?> billingKeyCheck(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        Map<String, Object> result = new HashMap<>();
        try {
            MemberDTO member = memberService.findMember(memberId);
            if(member.getBillingKey() != null) {
                result.put("billingKey", member.getBillingKey());
                result.put("status", true);
            } else {
                result.put("status", false);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchMemberException e){
            log.info(e.getMessage());
            log.info("회원정보 없음");
            result.put("status", false);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/billing/new")
    @Operation(description = "빌링키 등록")
    public ResponseEntity<?> billingKeyRegister(@RequestBody CardRequest card, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        Map<String, Object> result = new HashMap<>();
        String accessToken, billingKey, billingKeyEnc;
        try {
            accessToken = importAPI.getAccessToken();
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        try {
            billingKey = importAPI.getBillingKey(accessToken, card, memberId);
            billingKeyEnc = accountService.encrypt(billingKey);
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        memberService.updateBillingKey(memberId, billingKeyEnc);
        result.put("status", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/billing")
    @Operation(description = "빌링키 수정")
    public ResponseEntity<?> billingKeyUpdate(@RequestBody CardRequest card, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        Map<String, Object> result = new HashMap<>();
        String accessToken, billingKey, billingKeyEnc, customerUidEnc, customerUid;
        try {
            accessToken = importAPI.getAccessToken();
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        try {
            MemberDTO member = memberService.findMember(memberId);
            customerUidEnc = member.getBillingKey();
            customerUid = accountService.decrypt(customerUidEnc);
        } catch (Exception e){
            log.info(e.getMessage());
            log.info("회원정보 없음");
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        try {
            importAPI.deleteBillingKey(accessToken, customerUid);
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        try {
            billingKey = importAPI.getBillingKey(accessToken, card, memberId);
            billingKeyEnc = accountService.encrypt(billingKey);
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        memberService.updateBillingKey(memberId, billingKeyEnc);
        result.put("status", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/billing")
    @Operation(description = "빌링키 삭제")
    public ResponseEntity<?> billingKeyDelete(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        Map<String, Object> result = new HashMap<>();
        String accessToken, customerUidEnc, customerUid;
        try {
            accessToken = importAPI.getAccessToken();
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        try {
            MemberDTO member = memberService.findMember(memberId);
            customerUidEnc = member.getBillingKey();
            customerUid = accountService.decrypt(customerUidEnc);
            importAPI.deleteBillingKey(accessToken, customerUid);
            memberService.deleteBillingKey(memberId);
        } catch (Exception e){
            log.info(e.getMessage());
            result.put("status", false);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        result.put("status", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/bank")
    @Operation(description = "은행 조회")
    public ResponseEntity<?> BankCheck(HttpServletRequest req) {
        List<Bank> bankList = accountService.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("bankList", bankList);
        result.put("status", true);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
