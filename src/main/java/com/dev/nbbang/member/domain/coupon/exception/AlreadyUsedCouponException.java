package com.dev.nbbang.member.domain.coupon.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class AlreadyUsedCouponException extends RuntimeException {
    private final NbbangException nbbangException;

    public AlreadyUsedCouponException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public AlreadyUsedCouponException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

