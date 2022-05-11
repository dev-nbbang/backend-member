package com.dev.nbbang.member.domain.ott.dto.response;

import com.dev.nbbang.member.domain.ott.dto.OttViewDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OttViewResponse {
    private List<OttViewDTO> ottView;

    @Builder
    public OttViewResponse(List<OttViewDTO> ottView) {
        this.ottView = ottView;
    }

    public static OttViewResponse create(List<OttViewDTO> ottView) {
        return OttViewResponse.builder()
                .ottView(ottView)
                .build();
    }
}
