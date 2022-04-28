package com.dev.nbbang.member.domain.point.entity;

import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "POINT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POINT_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column(name = "POINT_YMD")
    private LocalDateTime pointYmd;

    @Column(name = "USE_POINT")
    private Long usePoint;

    @Column(name = "POINT_DETAIL")
    private String pointDetail;

    @Column(name = "POINT_TYPE")
    @Enumerated(EnumType.STRING)
    private PointType pointType;
}
