package com.dev.nbbang.member.domain.account.dto.request;

import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequest {
    private int bankId;
    private String bankAccount;

    public void encBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public static Member toEntity(AccountRequest request) {
        return Member.builder()
                .bankId(request.getBankId())
                .bankAccount(request.getBankAccount())
                .build();
    }
}
