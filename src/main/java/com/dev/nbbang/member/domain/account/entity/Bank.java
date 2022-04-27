package com.dev.nbbang.member.domain.account.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "BANK")
public class Bank {
    @Id
    @Column(name = "bank_id", nullable = false)
    private int bankId;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_img")
    private String bankImg;
}
