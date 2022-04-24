package com.dev.nbbang.member.domain.ott.entity;

import com.dev.nbbang.member.domain.ott.entity.OttView;
import com.dev.nbbang.member.domain.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "MEMBER_OTT")
public class MemberOtt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_ott_id")
    private Long memberOttId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "ott_id")
    private OttView ottView;

    // 양방향 연관관계 매핑
    public void addMember(Member member) {
        if(this.member != null) {
            member.getMemberOtt().remove(this);
        }
        this.member = member;
        member.getMemberOtt().add(this);        // new HashSet<> 초기화가 되었는데 왜 안될지 - @Builder.Default
    }

}

