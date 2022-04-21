package com.dev.nbbang.member.domain.user.api.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class FailCreateAuthUrlException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailCreateAuthUrlException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailCreateAuthUrlException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

