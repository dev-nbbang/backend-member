package com.dev.nbbang.member.domain.ott.dto.response;

import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OttViewResponse {
    private List<OttViewDTO> ottView;
    private boolean status;
    private String message;

    public static OttViewResponse createList(List<OttViewDTO> ottViews, boolean status,  String message) {
        return OttViewResponse.builder()
                .ottView(ottViews)
                .status(status)
                .message(message).build();
    }
}
