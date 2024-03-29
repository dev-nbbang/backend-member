package com.dev.nbbang.member.global.exception;

public enum NbbangException {
    NOT_FOUND_MEMBER ("BE001", "No Such a Member"),
    NO_CREATE_MEMBER("BE002", "Doesn't Create Member"),
    FAIL_TO_LOGOUT("BE003", "Failed To Logout And Delete Redis Token Data"),
    FAIL_TO_DELETE_MEMBER("BE004", "Failed To Delete Member"),
    FAIL_TO_CREATE_AUTH_URL("BE005", "Failed To Create Social Auth Url"),
    ILLEGAL_SOCIAL_TYPE("BE006", "Input Illegal Social Type"),
    DUPLICATE_MEMBER_ID("BE007", "Duplicate Member Id"),
    DUPLICATE_NICKNAME("BE008","Duplicate Nickname"),
    FAIL_TO_ENCRYPT("BE201", "Failed To Encrypt"),
    FAIL_TO_DECRYPT("BE202", "Failed To Decrypt"),
    FAIL_TO_IMPORT_SERVER("BE203", "Failed To Import Server"),
    FAIL_TO_ISSUE_BILLINGKEY("BE204", "Failed To Issue BillingKey"),
    FAIL_TO_DELETE_BILLINGKEY("BE205", "Failed To Delete BillingKey"),
    NOT_FOUND_OTT("BE301", "No Such a Ott"),
    NO_CREATE_MEMBER_OTT("BE302", "Doesn't create Member interest Ott"),
    NOT_FOUND_MEMBER_OTT("BE303", "No Such A Member Ott"),
    FAIL_TO_DELETE_MEMBER_OTT("BE304", "Fail To Delete Member Ott"),
    NOT_FOUND_COUPON("BE401", "No Such a Coupon"),
    DUPLICATION_COUPON("BE402", "Duplication a Coupon"),
    ALREADY_USED_COUPON("BE403", "Already Used a Coupon"),
    NO_CREATE_POINT_DETAILS("BE501", "Doesn't Create Point Details"),
    FAIL_CREDIT_RECOMMEND_POINT("BE502", "Fail Credit Recommend Point"),
    ILLEGAL_NICKNAME("BE503", "Illegal Nickname"), ;

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
