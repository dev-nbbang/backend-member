package com.dev.nbbang.member.domain.point.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class FailCreditRecommendPointException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailCreditRecommendPointException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailCreditRecommendPointException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

