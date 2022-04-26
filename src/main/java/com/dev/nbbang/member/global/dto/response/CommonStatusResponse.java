package com.dev.nbbang.member.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommonStatusResponse {
    private boolean status;

    public static CommonStatusResponse create(boolean status) {
        return CommonStatusResponse.builder().status(status).build();
    }

}