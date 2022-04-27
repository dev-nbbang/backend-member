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
    private List<Bank> bankList;

    public static BankListResponse create(List<Bank> bankList) {
        return BankListResponse.builder().bankList(bankList).build();
    }
}
