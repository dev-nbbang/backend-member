package com.dev.nbbang.member.domain.user.api.util;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;

public class SocialLoginIdUtil {
    private SocialLoginType socialLoginType;
    private Long id;
    private String memberId;
    public SocialLoginIdUtil(SocialLoginType socialLoginType, Long id) {
        this.socialLoginType = socialLoginType;
        this.id = id;
        this.memberId = makeSocialLoginId(socialLoginType, id);
    }

    public String makeSocialLoginId(SocialLoginType socialLoginType, Long id){
        switch (socialLoginType) {
            case KAKAO: return "K-"+id;
            default:    return "test-"+id;
        }
    }

    public String getMemberId() {
        return memberId;
    }
}
