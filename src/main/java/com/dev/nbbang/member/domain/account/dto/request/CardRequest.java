package com.dev.nbbang.member.domain.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardRequest {
    private String cardNumber;
    private String expiry;
    private String birth;
    private String pwd2digit;
}
