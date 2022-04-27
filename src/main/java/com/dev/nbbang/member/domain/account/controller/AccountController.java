package com.dev.nbbang.member.domain.account.controller;

import com.dev.nbbang.member.domain.account.dto.request.AccountRequest;
import com.dev.nbbang.member.domain.account.dto.response.AccountResponse;
import com.dev.nbbang.member.domain.account.dto.response.BankListResponse;
import com.dev.nbbang.member.domain.account.dto.response.BillingKeyResponse;
import com.dev.nbbang.member.domain.account.entity.Bank;
import com.dev.nbbang.member.domain.account.exception.*;
import com.dev.nbbang.member.domain.account.service.AccountService;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.account.api.service.ImportAPI;
import com.dev.nbbang.member.domain.account.dto.request.CardRequest;
import com.dev.nbbang.member.domain.user.service.MemberService;
import com.dev.nbbang.member.global.dto.response.CommonStatusResponse;
import com.dev.nbbang.member.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
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
        String msg = "";
        try {
            MemberDTO member = memberService.findMember(memberId);
            if(member.getBankId()!=null && member.getBankAccount()!=null) {
                Bank bank = accountService.findByBankId(member.getBankId());
                String bankAccount = accountService.decrypt(member.getBankAccount());
                return new ResponseEntity<>(AccountResponse.create(bank, bankAccount), HttpStatus.OK);
            } else {
                msg = "현재 계좌 정보가 없습니다";
                log.info(msg);
            }
        } catch (NoSuchMemberException | FailDecryptException e){
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(false), HttpStatus.OK);
    }

    @PostMapping("/new")
    @Operation(description = "계좌 등록")
    public ResponseEntity<?> accountRegister(@RequestBody AccountRequest accountRequest, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        boolean status = false;
        try {
            String bankAccountEnc = accountService.encrypt(accountRequest.getBankAccount());
            accountRequest.encBankAccount(bankAccountEnc);
            memberService.updateAccount(memberId, AccountRequest.toEntity(accountRequest));
            status = true;
        } catch (FailEncryptException | NoSuchMemberException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    @PutMapping("/")
    @Operation(description = "계좌 수정")
    public ResponseEntity<?> accountUpdate(@RequestBody AccountRequest accountRequest, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        boolean status = false;
        try {
            String bankAccountEnc = accountService.encrypt(accountRequest.getBankAccount());
            accountRequest.encBankAccount(bankAccountEnc);
            memberService.updateAccount(memberId, AccountRequest.toEntity(accountRequest));
            status = true;
        } catch (FailEncryptException | NoSuchMemberException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    @DeleteMapping("/")
    @Operation(description = "계좌 삭제")
    public ResponseEntity<?> accountDelete(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        boolean status = false;
        try {
            memberService.deleteAccount(memberId);
            status = true;
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    @GetMapping("/billing")
    @Operation(description = "빌링키 조회")
    public ResponseEntity<?> billingKeyCheck(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        try {
            MemberDTO member = memberService.findMember(memberId);
            if(member.getBillingKey() != null) {
                String biilingKey = accountService.decrypt(member.getBillingKey());
                return new ResponseEntity<>(BillingKeyResponse.create(biilingKey), HttpStatus.OK);
            }
        } catch (NoSuchMemberException e){
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(false), HttpStatus.OK);
    }

    @PostMapping("/billing/new")
    @Operation(description = "빌링키 등록")
    public ResponseEntity<?> billingKeyRegister(@RequestBody CardRequest card, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        String accessToken, billingKey, billingKeyEnc;
        boolean status = false;
        try {
            accessToken = importAPI.getAccessToken();
            billingKey = importAPI.getBillingKey(accessToken, card, memberId);
            billingKeyEnc = accountService.encrypt(billingKey);
            memberService.updateBillingKey(memberId, billingKeyEnc);
            status = true;
        } catch (FailImportServerException | FailIssueBillingKeyException | FailEncryptException | NoSuchMemberException e) {
            log.info("error: " + e.getMessage());
        }

        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    @PutMapping("/billing")
    @Operation(description = "빌링키 수정")
    public ResponseEntity<?> billingKeyUpdate(@RequestBody CardRequest card, HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        String accessToken, billingKey, billingKeyEnc, customerUidEnc, customerUid;
        boolean status = false;
        try {
            accessToken = importAPI.getAccessToken();
            MemberDTO member = memberService.findMember(memberId);
            customerUidEnc = member.getBillingKey();
            customerUid = accountService.decrypt(customerUidEnc);
            importAPI.deleteBillingKey(accessToken, customerUid);
            billingKey = importAPI.getBillingKey(accessToken, card, memberId);
            billingKeyEnc = accountService.encrypt(billingKey);
            memberService.updateBillingKey(memberId, billingKeyEnc);
            status = true;
        } catch (FailImportServerException | NoSuchMemberException | FailDecryptException | FailDeleteBillingKeyException | FailIssueBillingKeyException | FailEncryptException e) {
            log.info("error: " + e.getMessage());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    @DeleteMapping("/billing")
    @Operation(description = "빌링키 삭제")
    public ResponseEntity<?> billingKeyDelete(HttpServletRequest req) {
        String token = req.getHeader("Authorization").substring(7);
        String memberId = jwtUtil.getUserid(token);
        String accessToken, customerUidEnc, customerUid;
        boolean status = false;
        String msg = "";
        try {
            accessToken = importAPI.getAccessToken();
            MemberDTO member = memberService.findMember(memberId);
            if(member.getBillingKey() != null) {
                customerUidEnc = member.getBillingKey();
                customerUid = accountService.decrypt(customerUidEnc);
                importAPI.deleteBillingKey(accessToken, customerUid);
                memberService.deleteBillingKey(memberId);
                status = true;
            } else {
                msg = "billingKey가 존재하지 않습니다";
                log.info(msg);
            }
        } catch (FailImportServerException | NoSuchMemberException | FailDecryptException | FailDeleteBillingKeyException e) {
            log.info("error: " + e.getMessage());
            log.info("error: " + e.getClass());
        }
        return new ResponseEntity<>(CommonStatusResponse.create(status), HttpStatus.OK);
    }

    @GetMapping("/bank")
    @Operation(description = "은행 조회")
    public ResponseEntity<?> BankCheck(HttpServletRequest req) {
        List<Bank> bankList = accountService.findAll();
        return new ResponseEntity<>(BankListResponse.create(bankList), HttpStatus.OK);
    }

}
