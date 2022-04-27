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

    /**
     * 소셜 로그인 타입과 인가코드를 이용해 각 포털의 소셜 로그인을 통해 로그인한다.
     * @param socialLoginType - Enum 타입의 소셜 로그인 타입 (Google, kakao)
     * @param code - 각 소셜 로그인 콜백 URI에 리턴해주는 인가코드
     * @return memberId - 각 포털의 첫번째 이니셜과 제공하는 소셜 로그인 아이디를 합친 String 타입의 고유 아이디
     */
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

    /**
     * 회원 아이디를 이용해 가입된 회원 상세 내용을 찾는다.
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
     * @param nickname - String 타입의 닉네임
     * @return MemberDTO - 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    public MemberDTO findMemberByNickname(String nickname) {
        Member member = Optional.ofNullable(memberRepository.findByNickname(nickname))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));
        return MemberDTO.create(member);
    }

    /**
     * 회원의 상세내용과 관심 Ott 아이디를 이용해 회원을 저장하고 관심 Ott 서비스를 등록한다.
     * @param member - 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     * @param ottId - Integer 타입의 Ott 서비스 아이디 리스트
     * @return MemberDTO - 새로 저장된 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
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

    /**
     * 회원 아이디를 이용해 가입된 회원을 찾은 뒤 새로운 회원 정보로 수정하고 관심 OTT 서비스도 수정한다.
     * @param sessionMemberId - JWT 토큰으로 파싱한 세션 회원 아이디
     * @param member - 최신 정보로 수정될 회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     * @param ottId - Integer 타입의 Ott 서비스 아이디 리스트
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
     * @param nickname - String 타입의 회원 닉네임
     * @return boolean 타입 값
     */
    @Override
    public boolean duplicateNickname(String nickname) {
        MemberDTO member = findMemberByNickname(nickname);
        return member.getNickname().length() > 0;
    }

    /**
     * 닉네임을 이용해 해당 닉네임을 포함하고 있는 5명의 회원 리스트를 조회한다.
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
     * @param memberId - JWT 토큰에서 파싱한 회원 아이디
     */
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

    /**
     * Redis에 저장된 리프레시 토큰을 삭제시키고 서비스 로그아웃을 한다.
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
     * @param sessionMemberId - JWT 토큰에서 파싱한 회원 아이디
     * @param member - 새롭게 수정될 등급을 가진 회원 객체
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
     * @param sessionMemberId - JWT 토큰에서 파싱한 회원 아이디
     * @param member - 변경될 포인트를 가진 회원 객체
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

    /**
     * DTO 타입의 회원 객체를 이용해 Redis에서 관리할 리프레시 토큰과 세션에 저장할 엑세스 토큰을 관리한다.
     * @param member DTO 타입의 회원 객체
     * @return accessToken String 타입의 엑세스 토큰
     */
    @Override
    public String manageToken(MemberDTO member) {
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());
        redisUtil.setData(member.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

        return jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
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
