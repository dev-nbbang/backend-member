package com.dev.nbbang.member.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommonFailResponse {
    private boolean status;
    private String message;

    public static CommonFailResponse create(boolean status, String message) {
        return CommonFailResponse.builder()
                .status(status)
                .message(message)
                .build();
    }
}
