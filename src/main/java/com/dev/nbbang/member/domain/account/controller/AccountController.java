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
import com.dev.nbbang.member.global.dto.response.CommonResponse;
import com.dev.nbbang.member.global.dto.response.CommonSuccessResponse;
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

    @GetMapping("/")
    @Operation(description = "계좌 조회")
    public ResponseEntity<?> accountCheck(HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        String msg = "";

        try {
            MemberDTO member = memberService.findMember(memberId);
            if(member.getBankId()!=null && member.getBankAccount()!=null) {
                Bank bank = accountService.findByBankId(member.getBankId());
                String bankAccount = accountService.decrypt(member.getBankAccount());
                msg = "계좌 조회에 성공했습니다";
                return new ResponseEntity<>(CommonSuccessResponse.response(true, AccountResponse.create(bank, bankAccount), msg), HttpStatus.OK);
            } else {
                msg = "현재 계좌 정보가 없습니다";
                log.info(msg);
            }
        } catch (NoSuchMemberException | FailDecryptException e){
            msg = "서버 오류 입니다";
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, msg), HttpStatus.OK);
    }

    @PostMapping("/new")
    @Operation(description = "계좌 등록")
    public ResponseEntity<?> accountRegister(@RequestBody AccountRequest accountRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        try {
            String bankAccountEnc = accountService.encrypt(accountRequest.getBankAccount());
            accountRequest.encBankAccount(bankAccountEnc);
            memberService.updateAccount(memberId, AccountRequest.toEntity(accountRequest));
            return new ResponseEntity<>(CommonResponse.create(true, "계좌 등록을 완료했습니다"), HttpStatus.CREATED);
        } catch (FailEncryptException | NoSuchMemberException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "계좌 등록에 실패했습니다"), HttpStatus.OK);
    }

    @PutMapping("/")
    @Operation(description = "계좌 수정")
    public ResponseEntity<?> accountUpdate(@RequestBody AccountRequest accountRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        try {
            String bankAccountEnc = accountService.encrypt(accountRequest.getBankAccount());
            accountRequest.encBankAccount(bankAccountEnc);
            memberService.updateAccount(memberId, AccountRequest.toEntity(accountRequest));
            return new ResponseEntity<>(CommonResponse.create(true, "계좌 수정을 완료했습니다"), HttpStatus.CREATED);
        } catch (FailEncryptException | NoSuchMemberException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "계좌 수정에 실패했습니다"), HttpStatus.OK);
    }

    @DeleteMapping("/")
    @Operation(description = "계좌 삭제")
    public ResponseEntity<?> accountDelete(HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        try {
            memberService.deleteAccount(memberId);
            return new ResponseEntity<>(CommonResponse.create(true, "계좌 삭제를 완료했습니다"), HttpStatus.NO_CONTENT);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "계좌 삭제에 실패했습니다"), HttpStatus.OK);
    }

    @GetMapping("/billing")
    @Operation(description = "빌링키 조회")
    public ResponseEntity<?> billingKeyCheck(HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        try {
            MemberDTO member = memberService.findMember(memberId);
            if(member.getBillingKey() != null) {
                String biilingKey = accountService.decrypt(member.getBillingKey());
                return new ResponseEntity<>(CommonSuccessResponse.response(true, BillingKeyResponse.create(biilingKey), "빌링키 조회를 완료했습니다"), HttpStatus.OK);
            }
        } catch (NoSuchMemberException e){
            log.info(e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "빌링키 조회를 실패했습니다"), HttpStatus.OK);
    }

    @PostMapping("/billing/new")
    @Operation(description = "빌링키 등록")
    public ResponseEntity<?> billingKeyRegister(@RequestBody CardRequest card, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        String accessToken, billingKey, billingKeyEnc;
        try {
            accessToken = importAPI.getAccessToken();
            billingKey = importAPI.getBillingKey(accessToken, card, memberId);
            billingKeyEnc = accountService.encrypt(billingKey);
            memberService.updateBillingKey(memberId, billingKeyEnc);
            return new ResponseEntity<>(CommonResponse.create(true, "빌링키 등록을 완료했습니다"), HttpStatus.CREATED);
        } catch (FailImportServerException | FailIssueBillingKeyException | FailEncryptException | NoSuchMemberException e) {
            log.info("error: " + e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "빌링키 등록에 실패했습니다"), HttpStatus.OK);
    }

    @PutMapping("/billing")
    @Operation(description = "빌링키 수정")
    public ResponseEntity<?> billingKeyUpdate(@RequestBody CardRequest card, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        String accessToken, billingKey, billingKeyEnc, customerUidEnc, customerUid;
        try {
            accessToken = importAPI.getAccessToken();
            MemberDTO member = memberService.findMember(memberId);
            customerUidEnc = member.getBillingKey();
            customerUid = accountService.decrypt(customerUidEnc);
            importAPI.deleteBillingKey(accessToken, customerUid);
            billingKey = importAPI.getBillingKey(accessToken, card, memberId);
            billingKeyEnc = accountService.encrypt(billingKey);
            memberService.updateBillingKey(memberId, billingKeyEnc);
            return new ResponseEntity<>(CommonResponse.create(true, "빌링키 수정을 완료했습니다"), HttpStatus.CREATED);
        } catch (FailImportServerException | NoSuchMemberException | FailDecryptException | FailDeleteBillingKeyException | FailIssueBillingKeyException | FailEncryptException e) {
            log.info("error: " + e.getMessage());
        }
        return new ResponseEntity<>(CommonResponse.create(false, "빌링키 수정에 실패했습니다"), HttpStatus.OK);
    }

    @DeleteMapping("/billing")
    @Operation(description = "빌링키 삭제")
    public ResponseEntity<?> billingKeyDelete(HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        String accessToken, customerUidEnc, customerUid;
        String msg = "";
        try {
            accessToken = importAPI.getAccessToken();
            MemberDTO member = memberService.findMember(memberId);
            if(member.getBillingKey() != null) {
                customerUidEnc = member.getBillingKey();
                customerUid = accountService.decrypt(customerUidEnc);
                importAPI.deleteBillingKey(accessToken, customerUid);
                memberService.deleteBillingKey(memberId);
                return new ResponseEntity<>(CommonResponse.create(true, "빌링키 삭제를 완료했습니다"), HttpStatus.NO_CONTENT);
            } else {
                msg = "billingKey가 존재하지 않습니다";
                log.info(msg);
            }
        } catch (FailImportServerException | NoSuchMemberException | FailDecryptException | FailDeleteBillingKeyException e) {
            log.info("error: " + e.getMessage());
            log.info("error: " + e.getClass());
            msg = "빌링키 삭제에 실패했습니다";
        }
        return new ResponseEntity<>(CommonResponse.create(false, msg), HttpStatus.OK);
    }

    @GetMapping("/bank")
    @Operation(description = "은행 조회")
    public ResponseEntity<?> BankCheck(HttpServletRequest req) {
        List<Bank> bankList = accountService.findAll();
        return new ResponseEntity<>(CommonSuccessResponse.response(true, BankListResponse.create(bankList), "은행 조회에 성공했습니다"), HttpStatus.OK);
    }

}
