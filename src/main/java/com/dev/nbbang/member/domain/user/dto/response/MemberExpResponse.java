package com.dev.nbbang.member.domain.user.dto.response;

import com.dev.nbbang.member.domain.user.dto.MemberDTO;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberExpResponse {
    private String memberId;
    private Long exp;
    private boolean status;
    private String message;

    public static Map<String, Object> create(MemberDTO member, boolean status, String message)  {
        MemberExpResponse memberInfo = MemberExpResponse.builder()
                .memberId(member.getMemberId())
                .exp(member.getExp())
                .build();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberInfo", memberInfo);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status);
        responseMap.put("message", message);
        responseMap.put("data", dataMap);

        return responseMap;
    }

/*    public static MemberExpResponse create(MemberDTO member, boolean status, String message) {
        return MemberExpResponse.builder()
                .memberId(member.getMemberId())
                .exp(member.getExp())
                .status(status)
                .message(message)
                .build();
    }*/
}
