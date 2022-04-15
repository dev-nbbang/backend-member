package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.dto.request.MemberGradeRequest;
import com.dev.nbbang.member.domain.user.dto.response.MemberResponse;
import com.dev.nbbang.member.domain.user.dto.response.NicknameMemberResponse;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.user.repository.OTTViewRepository;
import com.dev.nbbang.member.domain.user.api.util.SocialLoginIdUtil;
import com.dev.nbbang.member.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final OTTViewRepository ottViewRepository;
    private final SocialTypeMatcher socialTypeMatcher;

    // 소셜타입마다 각각 다른 소셜 로그인
    public String socialLogin(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = socialTypeMatcher.findSocialOauthByType(socialLoginType);
        try {
            String socialLoginId = socialOauth.requestUserInfo(code);
            SocialLoginIdUtil socialLoginIdUtil = new SocialLoginIdUtil(socialLoginType, socialLoginId);
            return socialLoginIdUtil.getMemberId();
        } catch (Exception e) {
            e.printStackTrace();
            return "소셜 로그인 실패";
        }
    }

    // 회원 아이디로 회원 찾기
    @Override
    public MemberDTO findMember(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.create(member);
    }

    // 닉네임으로 회원 찾기
    @Override
    public MemberDTO findMemberByNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.create(member);
    }

    // 추가 회원 정보 저장하기
    @Override
    public MemberDTO memberSave(Member member) {
        Optional<Member> savedMember = Optional.ofNullable(memberRepository.save(member));
        return MemberDTO.create(savedMember.orElseThrow(() -> new NoCreateMemberException("회원가입에 실패했습니다.", NbbangException.NO_CREATE_MEMBER)));
    }

    @Override
    public boolean duplicateNickname(String nickname) {
        MemberDTO member = findMemberByNickname(nickname);
        return member.getNickname().length() > 0;
    }

    @Override
    public List<MemberDTO> findMemberListByNickname(String nickname) {
        List<Member> findMemberList = memberRepository.findTop5ByNicknameStartingWith(nickname).orElseThrow(
                () -> new NoSuchMemberException("해당 닉네임을 갖는 회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.createList(findMemberList);
    }

    //
    public OTTView findByOttId(int ottId) {
        return ottViewRepository.findByOttId(ottId);
    }
}
