package com.dev.nbbang.member.domain.account.dto.response;

import com.dev.nbbang.member.domain.account.entity.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private Bank bank;
    private String bankAccount;

    public static AccountResponse create(Bank bank, String bankAccount) {
        return AccountResponse.builder().bank(bank).bankAccount(bankAccount).build();
    }
}
