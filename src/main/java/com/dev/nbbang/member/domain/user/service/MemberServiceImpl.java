package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.user.entity.OTTView;
import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.domain.user.exception.FailLogoutMemberException;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.user.repository.OTTViewRepository;
import com.dev.nbbang.member.domain.user.api.util.SocialLoginIdUtil;
import com.dev.nbbang.member.global.exception.NbbangException;
import com.dev.nbbang.member.global.util.JwtUtil;
import com.dev.nbbang.member.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final OTTViewRepository ottViewRepository;
    private final SocialTypeMatcher socialTypeMatcher;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

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
    @Transactional
    public MemberDTO saveMember(Member member) {
        Optional<Member> savedMember = Optional.ofNullable(memberRepository.save(member));
        return MemberDTO.create(savedMember.orElseThrow(() -> new NoCreateMemberException("회원정보 저장에 실패했습니다.", NbbangException.NO_CREATE_MEMBER)));
    }

    // 회원 정보 업데이트
    @Override
    @Transactional
    public MemberDTO updateMember(String sessionMemberId, Member member) {
        Member findMember = memberRepository.findByMemberId(sessionMemberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateMember(findMember.getMemberId(),member.getNickname(), member.getOttView(), member.getPartyInviteYn());
        return MemberDTO.create(findMember);
    }

    // 닉네임 중복 확인
    @Override
    public boolean duplicateNickname(String nickname) {
        MemberDTO member = findMemberByNickname(nickname);
        return member.getNickname().length() > 0;
    }

    //닉네임 리스트 가져오기
    @Override
    public List<MemberDTO> findMemberListByNickname(String nickname) {
        List<Member> findMemberList = memberRepository.findTop5ByNicknameStartingWith(nickname).orElseThrow(
                () -> new NoSuchMemberException("해당 닉네임을 갖는 회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.createList(findMemberList);
    }

    // 회원 탈퇴
    @Override
    public void deleteMember(String memberId) {

        if (memberId.length() < 1)
            throw new FailDeleteMemberException("회원탈퇴에 실패했습니다.", NbbangException.FAIL_TO_DELETE_MEMBER);

        // 레디스 토큰 삭제
        if (!redisUtil.deleteData(memberId))
            throw new FailDeleteMemberException("회원탈퇴에 실패했습니다.", NbbangException.FAIL_TO_DELETE_MEMBER);

        // 소셜에서 회원 삭제 (카카오는 연동 해제)

        // 다른 서비스에 이벤트 발행 후 관련 모든 데이터 지우기 (동일 서비스 cascade로 관련 테이블 모두 지우기)
        memberRepository.deleteByMemberId(memberId);

    }

    @Override
    public boolean logout(String memberId) {
        if (memberId.length() < 1) throw new FailLogoutMemberException("로그아웃에 실패했습니다.", NbbangException.FAIL_TO_LOGOUT);
        // 존재하지 않는 회원까지 이중 체크?
        return redisUtil.deleteData(memberId);
    }

    // 회원 등급 수정
    @Override
    @Transactional
    public MemberDTO updateGrade(String sessionMemberId, Member member) {
        Member findMember = memberRepository.findByMemberId(sessionMemberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateMember(findMember.getMemberId(), member.getGrade());
        return MemberDTO.create(findMember);
    }

    // 회원 경험치 변경
    @Override
    @Transactional
    public MemberDTO updateExp(String sessionMemberId, Member member) {
        Member findMember = memberRepository.findByMemberId(sessionMemberId).orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateMember(findMember.getMemberId(), member.getExp());
        return MemberDTO.create(findMember);
    }

    // 엑세스 토큰, 리프레시 토큰 관리
    @Override
    public String manageToken(MemberDTO member) {
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());
        redisUtil.setData(member.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

        return jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
    }
    public OTTView findByOttId(int ottId) {
        return ottViewRepository.findByOttId(ottId);
    }
}
