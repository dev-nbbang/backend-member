package com.dev.nbbang.member.global.exception;

public enum NbbangException {
    NOT_FOUND_MEMBER ("BE001", "No Such a Member"),
    NO_CREATE_MEMBER("BE002", "Doesn't Create Member"),
    FAIL_TO_LOGOUT("BE003", "Failed To Logout And Delete Redis Token Data"),
    FAIL_TO_DELETE_MEMBER("BE004", "Failed To Delete Member"),;

    private String code;
    private String message;

    NbbangException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
