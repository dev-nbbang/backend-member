package com.dev.nbbang.member.domain.account.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class FailEncryptException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailEncryptException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailEncryptException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

