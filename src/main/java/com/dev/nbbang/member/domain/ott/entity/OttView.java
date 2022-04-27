package com.dev.nbbang.member.domain.ott.entity;

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
@AllArgsConstructor
@Builder
@Table(name = "OTTVIEW")
public class OttView {
    @Id
    @Column(name = "ott_id", nullable = false)
    private Integer ottId;

    @Column(name = "ott_name")
    private String ottName;

    @Column(name = "ott_image")
    private String ottImage;

    @Builder
    public OttView(int ottId, String ottName, String ottImage) {
        this.ottId = ottId;
        this.ottName = ottName;
        this.ottImage = ottImage;
    }
}
