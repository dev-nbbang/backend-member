package com.dev.nbbang.member.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "OTTVIEW")
public class OTTView {
    @Id
    @Column(name = "ott_id", nullable = false)
    private int ottId;
    @Column(name = "ott_name")
    private String ottName;
    @Column(name = "ott_image")
    private String ottImage;

    @Builder
    public OTTView(int ottId, String ottName, String ottImage) {
        this.ottId = ottId;
        this.ottName = ottName;
        this.ottImage = ottImage;
    }
}
