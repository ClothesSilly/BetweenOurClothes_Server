package com.betweenourclothes.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "authentication")
public class Email {

    @Id
    private String email;
    @Column(length=6)
    private String code;

    private String status;

    @Builder
    public Email(String email, String code, String status){
        this.email = email;
        this.code = code;
        this.status = status;
    }

    public void updateStatus(String status){
        this.status = status;
    }
}
