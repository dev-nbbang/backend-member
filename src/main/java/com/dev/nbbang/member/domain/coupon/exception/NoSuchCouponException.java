package com.dev.nbbang.member.domain.coupon.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class NoSuchCouponException extends RuntimeException {
    private final NbbangException nbbangException;

    public NoSuchCouponException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public NoSuchCouponException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

