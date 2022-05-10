package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberNicknameResponse {
    private String memberId;
    private String nickname;

    @Builder
    public MemberNicknameResponse(String memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }

    public static MemberNicknameResponse create(MemberDTO member) {
        return MemberNicknameResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .build();

    }

    public static List<MemberNicknameResponse> createList(List<MemberDTO> memberList) {
        List<MemberNicknameResponse> response = new ArrayList<>();
        for (MemberDTO memberDTO : memberList) {
            response.add(MemberNicknameResponse.builder()
                    .memberId(memberDTO.getMemberId())
                    .nickname(memberDTO.getNickname())
                    .build());
        }
        return response;
    }
}
