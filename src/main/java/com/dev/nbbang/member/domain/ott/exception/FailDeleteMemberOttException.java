package com.dev.nbbang.member.domain.ott.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class FailDeleteMemberOttException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailDeleteMemberOttException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailDeleteMemberOttException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

