package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import com.dev.nbbang.member.domain.user.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberGradeResponse {
    private String memberId;
    private Grade grade;

    public static Map<String, Object> create(MemberDTO member, boolean status, String message) {
        MemberGradeResponse memberInfo = MemberGradeResponse.builder()
                .memberId(member.getMemberId())
                .grade(member.getGrade())
                .build();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberInfo", memberInfo);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status);
        responseMap.put("message", message);
        responseMap.put("data", dataMap);

        return responseMap;
    }
}
