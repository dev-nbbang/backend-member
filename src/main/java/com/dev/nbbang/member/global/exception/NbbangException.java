package com.dev.nbbang.member.global.exception;

public enum NbbangException {
    NOT_FOUND_MEMBER ("BE001", "No Such a Member"),
    NO_CREATE_MEMBER("BE002", "Doesn't Create Member"),
    FAIL_TO_LOGOUT("BE003", "Failed To Logout And Delete Redis Token Data"),
    FAIL_TO_DELETE_MEMBER("BE004", "Failed To Delete Member"),
    FAIL_TO_CREATE_AUTH_URL("BE005", "Failed To Create Social Auth Url"),
    ILLEGAL_SOCAIL_TYPE("BE006", "Input Illegal Social Type"),
    NOT_FOUND_OTT("BE301", "No Such a Ott"),
    NO_CREATE_MEMBER_OTT("BE302", "Doesn't create Member interest Ott"),
    NOT_FOUND_MEMBER_OTT("BE303", "No Such A MEmber Ott"),;
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
