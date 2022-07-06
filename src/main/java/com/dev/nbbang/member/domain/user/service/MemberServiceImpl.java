package com.dev.nbbang.member.domain.user.service;

import com.dev.nbbang.member.domain.ott.dto.MemberOttDTO;
import com.dev.nbbang.member.domain.ott.entity.MemberOtt;
import com.dev.nbbang.member.domain.ott.exception.NoSuchOttException;

import com.dev.nbbang.member.domain.user.api.entity.SocialType;
import com.dev.nbbang.member.domain.user.api.service.SocialOauth;
import com.dev.nbbang.member.domain.user.api.util.SocialTypeMatcher;
import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;
import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.exception.*;
import com.dev.nbbang.member.domain.user.repository.MemberRepository;
import com.dev.nbbang.member.domain.ott.repository.OttViewRepository;
import com.dev.nbbang.member.domain.user.util.NicknameValidation;
import com.dev.nbbang.member.global.exception.NbbangException;
import com.dev.nbbang.member.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final OttViewRepository ottViewRepository;
    private final SocialTypeMatcher socialTypeMatcher;
    private final RedisUtil redisUtil;

    /**
     * 회원 아이디를 이용해 가입된 회원 상세 내용을 찾는다.
     *
     * @param memberId - JWT 토큰으로 파싱한 회원 아이디
     * @return MemberDTO - 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    public MemberDTO findMember(String memberId) {
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        return MemberDTO.create(member);
    }

    /**
     * 회원 닉네임을 이용해 가입된 회원 상세 내용을 찾는다.
     *
     * @param nickname - String 타입의 닉네임
     * @return MemberDTO - 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    public MemberDTO findMemberByNickname(String nickname) {
        Member member = Optional.ofNullable(memberRepository.findByNickname(nickname))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.create(member);
    }

    // 회원 정보 업데이트 (Member_Ott 구분)

    /**
     * 회원 아이디를 이용해 가입된 회원을 찾은 뒤 새로운 회원 정보로 수정하고 관심 OTT 서비스도 수정한다.
     *
     * @param sessionMemberId - JWT 토큰으로 파싱한 세션 회원 아이디
     * @param member          - 최신 정보로 수정될 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     * @param ottId           - Integer 타입의 Ott 서비스 아이디 리스트
     * @return MemberDTO - 최신 정보로 수정된 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
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

    /**
     * 닉네임을 이용해 가입된 회원인 여부를 판단한다.
     *
     * @param nickname - String 타입의 회원 닉네임
     * @return boolean 타입 값
     */
    @Override
    public boolean duplicateNickname(String nickname) {
        // 특수문자 및 공백 제외
        if (NicknameValidation.valid(nickname))
            throw new IllegalNicknameException("옳바르지 않은 닉네임입니다.", NbbangException.ILLEGAL_NICKNAME);


        Optional.ofNullable(memberRepository.findByNickname(nickname)).ifPresent(
                exception -> {
                    throw new DuplicateNicknameException("이미 사용중인 닉네임입니다.", NbbangException.DUPLICATE_NICKNAME);
                }
        );

        return true;
    }

    /**
     * 닉네임을 이용해 해당 닉네임을 포함하고 있는 5명의 회원 리스트를 조회한다.
     *
     * @param nickname - String 타입의 회원 닉네임
     * @return MemberDTO - 조회한 회원의 상세내용 리스트(memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    public List<MemberDTO> findMemberListByNickname(String nickname) {
        List<Member> findMemberList = Optional.ofNullable(memberRepository.findTop5ByNicknameStartingWith(nickname))
                .orElseThrow(() -> new NoSuchMemberException("해당 닉네임을 갖는 회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.createList(findMemberList);
    }

    /**
     * 회원 아이디를 통해 회원 탈퇴를 진행하고, 회원 데이터를 삭제한다.
     *
     * @param memberId - JWT 토큰에서 파싱한 회원 아이디
     */
    @Override
    @Transactional
    public void deleteMember(String memberId) {
        if (memberId.length() < 1)
            throw new FailDeleteMemberException("회원탈퇴에 실패했습니다.", NbbangException.FAIL_TO_DELETE_MEMBER);

        final String SOCIAL_TOKEN_PREFIX = "social-token:";

        // 소셜 연동 해제 (일단 카카오만)
        if(memberId.startsWith("K-") || memberId.startsWith("G-")) {
            SocialOauth socialOauth = socialTypeMatcher.findSocialOauth(memberId, SocialType.KAKAO);
            // 1. 소셜 타입 찾기
//        if(memberId.startsWith("G-")) socialOauth = socialTypeMatcher.findSocialOauth(memberId, SocialType.GOOGLE);

            // 2. 리프레시 토큰으로 엑세스 토큰 재발급
            String accessToken = socialOauth.generateAccessToken(redisUtil.getData(SOCIAL_TOKEN_PREFIX + memberId));

            // 3. 엑세스 토큰을 이용해 소셜 연동 해제 요청
            Boolean unlink = socialOauth.unlinkSocial(memberId, accessToken);

            if (!unlink) {
                throw new FailDeleteMemberException("소셜 연동 해제에 실패했습니다.", NbbangException.FAIL_TO_DELETE_MEMBER);
            }

            // 4. 레디스 JWT 토큰 삭제 및 소셜 토큰 삭제 (현재 일반 회원 삭제 안됌)
            if (!redisUtil.deleteData(memberId) || !redisUtil.deleteData(SOCIAL_TOKEN_PREFIX + memberId)) {
                throw new FailDeleteMemberException("회원탈퇴에 실패했습니다.", NbbangException.FAIL_TO_DELETE_MEMBER);
            }
        }
        // [TEST] 일반 회원의 경우는 그냥 JWT만 탈퇴하도록
        else {
            if (!redisUtil.deleteData(memberId)) {
                throw new FailDeleteMemberException("테스트! 일반 회원 탈퇴에 실패했습니다.", NbbangException.FAIL_TO_DELETE_MEMBER);
            }
        }

        // 5. 회원 서비스 데이터 삭제 진행
        memberRepository.deleteByMemberId(memberId);
    }

    /**
     * Redis에 저장된 리프레시 토큰을 삭제시키고 서비스 로그아웃을 한다.
     *
     * @param memberId - JWT 토크에서 파싱한 회원 아이디
     * @return boolean 타입의 값
     */
    @Override
    public boolean logout(String memberId) {
        if (memberId.length() < 1) throw new FailLogoutMemberException("로그아웃에 실패했습니다.", NbbangException.FAIL_TO_LOGOUT);
        // 존재하지 않는 회원까지 이중 체크?
        return redisUtil.deleteData(memberId);
    }

    /**
     * 회원 아이디를 이용해 새로운 등급을 수정한다.
     *
     * @param sessionMemberId - JWT 토큰에서 파싱한 회원 아이디
     * @param member          - 새롭게 수정될 등급을 가진 회원 객체
     * @return MemberDTO 최신 정보로 수정된 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    @Transactional
    public MemberDTO updateGrade(String sessionMemberId, Member member) {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(sessionMemberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateMember(findMember.getMemberId(), member.getGrade());
        return MemberDTO.create(findMember);
    }

    /**
     * 회원 아이디를 이용해 포인트를 변경시킨다.
     *
     * @param sessionMemberId - JWT 토큰에서 파싱한 회원 아이디
     * @param member          - 변경될 포인트를 가진 회원 객체
     * @return MemberDTO 최신 정보로 수정된 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    @Transactional
    public MemberDTO updateExp(String sessionMemberId, Member member) {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(sessionMemberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateMember(findMember.getMemberId(), member.getExp());
        return MemberDTO.create(findMember);
    }

    // 회원 계좌 수정
    @Override
    @Transactional
    public void updateAccount(String memberId, Member member) {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateAccountMember(findMember.getMemberId(), member.getBankId(), member.getBankAccount());
    }

    @Override
    @Transactional
    public void deleteAccount(String memberId) {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateAccountMember(findMember.getMemberId(), null, null);
    }

    //회원 빌링키 수정
    @Override
    @Transactional
    public void updateBillingKey(String memberId, String billingKey) {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateAccountMember(findMember.getMemberId(), billingKey);
    }

    //회원 빌링키 삭제
    @Override
    @Transactional
    public void deleteBillingKey(String memberId) {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        findMember.updateAccountMember(findMember.getMemberId(), null);
    }
}
