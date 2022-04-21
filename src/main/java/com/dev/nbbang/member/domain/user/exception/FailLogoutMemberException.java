package com.dev.nbbang.member.domain.user.exception;

import com.dev.nbbang.member.global.exception.NbbangException;

public class FailLogoutMemberException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailLogoutMemberException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailLogoutMemberException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }
}
