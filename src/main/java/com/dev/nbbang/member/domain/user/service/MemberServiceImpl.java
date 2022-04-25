package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.exception.NoCreatedMemberOttException;
import com.dev.nbbang.member.domain.ott.repository.MemberOttRepository;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.member.domain.user.api.entity.SocialLoginType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.exception.FailDeleteMemberException;
import com.dev.nbbang.member.domain.user.exception.FailLogoutMemberException;
import com.dev.nbbang.member.domain.user.exception.NoCreateMemberException;
import com.dev.nbbang.member.domain.user.exception.NoSuchMemberException;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
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
    private final MemberOttRepository memberOttRepository;
    private final OttViewRepository ottViewRepository;
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
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        return MemberDTO.create(member);
    }

    // 닉네임으로 회원 찾기
    @Override
    public MemberDTO findMemberByNickname(String nickname) {
        Member member = Optional.ofNullable(memberRepository.findByNickname(nickname))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.create(member);
    }

    // 추가 회원 정보 저장 하기(Member_Ott 구분)
    @Override
    @Transactional
    public MemberDTO saveMember(Member member, List<Integer> ottId) {
        // 1. 회원 저장
        Member savedMember = Optional.ofNullable(memberRepository.save(member))
                .orElseThrow(() -> new NoCreateMemberException("회원정보 저장에 실패했습니다.", NbbangException.NO_CREATE_MEMBER));

        // 2. OTT 찾기
        List<OttView> findOttViews = Optional.ofNullable(ottViewRepository.findAllByOttIdIn(ottId))
                .orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 플랫폼입니다.", NbbangException.NOT_FOUND_OTT));

        // 3. MemberOtt 엔티티로 변경
        List<MemberOtt> memberOttList = MemberOttDTO.toEntityList(savedMember, findOttViews);

        // 4. 양방향 연관 관계 매핑 (회원 - 회원OTT 양방향 관계 매핑)
        for (MemberOtt memberOtt : memberOttList) {
            memberOtt.addMember(savedMember);
        }

        // 5. 관심 OTT 저장
        List<MemberOtt> savedMemberOtt = Optional.of(memberOttRepository.saveAll(memberOttList))
                .orElseThrow(() -> new NoCreatedMemberOttException("관심 OTT 등록을 실패했습니다.", NbbangException.NO_CREATE_MEMBER_OTT));

        return MemberDTO.create(savedMember);
    }

    // 회원 정보 업데이트 (Member_Ott 구분)
    @Override
    @Transactional
    public MemberDTO updateMember(String sessionMemberId, Member member, List<Integer> ottId) {
        // 1. 회원 찾기
        Member updatedMember = Optional.ofNullable(memberRepository.findByMemberId(sessionMemberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        // 2. OTT 찾기 (회원 아이디를 가지고 getMemberOtt - list로 가져오기
        List<OttView> updatedOttViews = Optional.ofNullable(ottViewRepository.findAllByOttIdIn(ottId))
                .orElseThrow(() -> new NoSuchOttException("존재하지 않는 OTT 플랫폼입니다.", NbbangException.NOT_FOUND_OTT));

        // 3. 회원으로 불러온 Member OTT 지워주기 (이부분 Member OTT 로 업데이트 확인하기)
        List<MemberOtt> updatedMemberOtt = MemberOttDTO.toEntityList(updatedMember, updatedOttViews);

        // 4. 회원 및 관심 OTT 관계 업데이트
        updatedMember.updateMember(updatedMember.getMemberId(), member.getNickname(), member.getPartyInviteYn(), updatedMemberOtt);

        return MemberDTO.create(updatedMember);
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
        List<Member> findMemberList = Optional.ofNullable(memberRepository.findTop5ByNicknameStartingWith(nickname))
                .orElseThrow(() -> new NoSuchMemberException("해당 닉네임을 갖는 회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.createList(findMemberList);
    }

    // 회원 탈퇴
    @Override
    @Transactional
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
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(sessionMemberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateMember(findMember.getMemberId(), member.getGrade());
        return MemberDTO.create(findMember);
    }

    // 회원 경험치 변경
    @Override
    @Transactional
    public MemberDTO updateExp(String sessionMemberId, Member member) {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(sessionMemberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
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
}
