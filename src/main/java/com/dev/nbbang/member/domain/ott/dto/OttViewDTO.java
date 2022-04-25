package com.dev.nbbang.member.domain.ott.dto;

import com.dev.nbbang.member.domain.ott.entity.OttView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OttViewDTO {
    private int ottId;
    private String ottName;
    private String ottImage;

    public static OttViewDTO create(OttView ottView) {
        return OttViewDTO.builder()
                .ottId(ottView.getOttId())
                .ottName(ottView.getOttName())
                .ottImage(ottView.getOttImage())
                .build();
    }

    public static List<OttViewDTO> createList(List<OttView> ottViews) {
        List<OttViewDTO> ottView = new ArrayList<>();
        for (OttView view : ottViews) {
            ottView.add(OttViewDTO.builder()
                    .ottId(view.getOttId())
                    .ottName(view.getOttName())
                    .ottImage(view.getOttImage()).build());
        }

        return ottView;
    }
}
