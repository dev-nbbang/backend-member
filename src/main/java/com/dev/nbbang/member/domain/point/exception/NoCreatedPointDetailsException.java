package com.dev.nbbang.member.domain.point.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class NoCreatedPointDetailsException extends RuntimeException {
    private final NbbangException nbbangException;

    public NoCreatedPointDetailsException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public NoCreatedPointDetailsException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

