package com.dev.nbbang.member.domain.coupon.exception;

import com.dev.nbbang.member.global.exception.NbbangCommonException;
import com.dev.nbbang.member.global.exception.NbbangException;
import org.springframework.http.HttpStatus;

public class DuplicationCouponException extends NbbangCommonException {
    private final String message;
    private final NbbangException nbbangException;

    public DuplicationCouponException(String message, NbbangException nbbangException) {
        super(message);
        this.message = message;
        this.nbbangException = nbbangException;
    }

    @Override
    public String getErrorCode() {
        return nbbangException.getCode();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.OK;
    }

    @Override
    public String getMessage() {
        return message;
    }
}


