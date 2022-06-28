package com.dev.nbbang.member.domain.ott.exception;

import com.dev.nbbang.member.global.exception.NbbangCommonException;
import com.dev.nbbang.member.global.exception.NbbangException;
import org.springframework.http.HttpStatus;

public class NoCreatedMemberOttException extends NbbangCommonException {
    private final String message;
    private final NbbangException nbbangException;

    public NoCreatedMemberOttException(String message, NbbangException nbbangException) {
        super(message);
        this.message = message;
        this.nbbangException = nbbangException;
    }

    @Override
    public String getErrorCode() {
        return nbbangException.getCode();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.OK;
    }

    @Override
    public String getMessage() {
        return message;
    }
}


