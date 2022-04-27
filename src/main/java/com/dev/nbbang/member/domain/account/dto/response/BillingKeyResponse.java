package com.dev.nbbang.member.domain.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillingKeyResponse {
    private String billingKey;

    public static BillingKeyResponse create(String billingKey) {
        return BillingKeyResponse.builder().billingKey(billingKey).build();
    }
}
