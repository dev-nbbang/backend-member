package com.dev.nbbang.member.global.exception;

public enum NbbangException {
    NOT_FOUND_MEMBER ("BE001", "No Such a Member"),
    NO_CREATE_MEMBER("BE002", "Doesn't Create Member"),
    FAIL_TO_LOGOUT("BE003", "Failed To Logout And Delete Redis Token Data"),
    FAIL_TO_DELETE_MEMBER("BE004", "Failed To Delete Member"),
    FAIL_TO_ENCRYPT("BE201", "Failed To Encrypt"),
    FAIL_TO_DECRYPT("BE202", "Failed To Decrypt"),
    FAIL_TO_IMPORT_SERVER("BE203", "Failed To Import Server"),
    FAIL_TO_ISSUE_BILLINGKEY("BE204", "Failed To Issue BillingKey"),
    FAIL_TO_DELETE_BILLINGKEY("BE205", "Failed To Delete BillingKey"),
    NOT_FOUND_COUPON("BE401", "No Such a Coupon"),
    Duplication_Coupon("BE402", "Duplication a Coupon"),
    Already_Used_Coupon("BE403", "Already Used a Coupon");

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
