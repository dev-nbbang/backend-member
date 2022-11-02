package com.dev.nbbang.member.domain.account.service;

import com.dev.nbbang.member.domain.account.dto.response.BankListResponse;
import com.dev.nbbang.member.domain.account.entity.Bank;

import java.util.List;

public interface AccountService {
    Bank findByBankId(int bankId);
    List<BankListResponse> findAll();
    String encrypt(String text);
    String decrypt(String cipherText);
}
