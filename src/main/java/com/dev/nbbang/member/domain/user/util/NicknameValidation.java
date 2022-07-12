package com.dev.nbbang.member.domain.user.util;

import java.util.regex.Pattern;

public class NicknameValidation {
    public static Boolean valid(String nickname) {
        // 특수문자 및 공백 제외
        return !Pattern.compile("^[0-9|a-z|A-Z|가-힣]*$").matcher(nickname).find() || nickname.length() < 3 || nickname.length() > 10;
    }
}
