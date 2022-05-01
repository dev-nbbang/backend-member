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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberNicknameResponse {
    private String memberId;
    private String nickname;
    private boolean status;
    private String message;

    public static MemberNicknameResponse create(MemberDTO member, boolean status, String message) {
        return MemberNicknameResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .status(status)
                .message(message).build();

    }

    public static List<MemberNicknameResponse> createList(List<MemberDTO> memberList, boolean status) {
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
