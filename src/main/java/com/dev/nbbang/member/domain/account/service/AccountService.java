package com.dev.nbbang.member.domain.account.service;

import com.dev.nbbang.member.domain.account.entity.Bank;

import java.util.List;

public interface AccountService {
    Bank findByBankId(int bankId);
    List<Bank> findAll();
    String encrypt(String text) throws Exception;
    String decrypt(String cipherText) throws Exception;
}
