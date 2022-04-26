package com.dev.nbbang.member.domain.account.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class FailIssueBillingKeyException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailIssueBillingKeyException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailIssueBillingKeyException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

