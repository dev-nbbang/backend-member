package com.dev.nbbang.member.domain.coupon.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class DuplicationCouponException extends RuntimeException {
    private final NbbangException nbbangException;

    public DuplicationCouponException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public DuplicationCouponException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

