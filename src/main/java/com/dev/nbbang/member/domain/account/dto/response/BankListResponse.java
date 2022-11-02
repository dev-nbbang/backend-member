package com.dev.nbbang.member.domain.account.dto.response;

import com.dev.nbbang.member.domain.account.entity.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankListResponse {
    private int bankId;
    private String bankName;
    private String bankImg;

    public static BankListResponse create(Bank bank) {
        return new BankListResponse(bank.getBankId(), bank.getBankName(), bank.getBankImg());
    }
}
