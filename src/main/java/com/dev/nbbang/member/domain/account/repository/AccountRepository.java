package com.dev.nbbang.member.domain.account.repository;

import com.dev.nbbang.member.domain.account.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Bank, Integer> {
    Bank findByBankId(int bankId);
    List<Bank> findAll();
}
