package com.dev.nbbang.member.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@ToString
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@Builder
@Table(name = "MEMBER")
public class Member implements UserDetails {
    @Id
    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "bank_id")
    private Integer bankId;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "grade")
    private String grade;

    @Column(name = "point")
    private Long point;

    @Column(name = "exp")
    private Long exp;

    @Column(name = "billing_key")
    private String billingKey;

    @Column(name = "party_invite_yn")
    private String partyInviteYn;

    @ManyToMany
    @JoinTable(name = "MEMBER_OTT",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "ott_id"))
    private List<OTTView> ottView;

    @PrePersist
    private void prePersist() {
        if (this.grade == null) grade = Grade.BRONZE.name();
        if (this.point == null) point = 0L;
        if (this.exp == null) exp = 0L;
        if (this.partyInviteYn == null) partyInviteYn = "Y";
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return memberId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}