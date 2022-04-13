package com.dev.nbbang.member.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MEMBER")
public class Member implements UserDetails {
    @Id
    @Column(name = "member_id", nullable = false)
    private String memberId;
    @Column(nullable = false)
    private String nickname;
    @Column(name = "bank_id", nullable = false)
    private int bankId;
    @Column(name = "bank_account")
    private String bankAccount;
    @Column(nullable = false)
    private String grade;
    @Column(nullable = false)
    private long point;
    @Column(nullable = false)
    private long exp;
    @Column(name = "billing_key")
    private String billingKey;
    @Column(name = "party_invite_yn", nullable = false)
    private char partyInviteYn;

    @ManyToMany
    @JoinTable(name="MEMBER_OTT",
        joinColumns = @JoinColumn(name="member_id"),
        inverseJoinColumns = @JoinColumn(name="ott_id"))
    private List<OTTView> ottView;

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
