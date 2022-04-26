package com.dev.nbbang.member.domain.account.service;

import com.dev.nbbang.member.domain.account.entity.Bank;
import com.dev.nbbang.member.domain.account.exception.FailDecryptException;
import com.dev.nbbang.member.domain.account.exception.FailEncryptException;
import com.dev.nbbang.member.domain.account.repository.AccountRepository;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    @Value("${aes.key}")
    private String key;

    @Override
    public Bank findByBankId(int bankId) {
        return accountRepository.findByBankId(bankId);
    }

    @Override
    public List<Bank> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public String encrypt(String text) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(key.substring(0, 16).getBytes("UTF-8")));
            return new String(Base64.encodeBase64(c.doFinal(text.getBytes("UTF-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new FailEncryptException("암호화 실패", NbbangException.FAIL_TO_ENCRYPT);
    }

    @Override
    public String decrypt(String cipherText) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(key.substring(0, 16).getBytes("UTF-8")));
            return new String(c.doFinal(Base64.decodeBase64(cipherText.getBytes("UTF-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new FailDecryptException("복호화 실패", NbbangException.FAIL_TO_DECRYPT);
    }
}
