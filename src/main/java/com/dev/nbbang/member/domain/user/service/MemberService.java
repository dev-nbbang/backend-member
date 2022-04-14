package com.dev.nbbang.member.domain.user.service;


import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.dto.response.MemberResponse;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;

import java.util.Optional;

public interface MemberService {
    String socialLogin(SocialLoginType socialLoginType, String code);

    MemberResponse findMember(String memberId);

    Member memberSave(Member member);

    OTTView findByOttId(int ottId);

}
