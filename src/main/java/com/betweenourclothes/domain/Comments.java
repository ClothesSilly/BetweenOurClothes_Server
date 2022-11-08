package com.betweenourclothes.domain;

import com.betweenourclothes.domain.members.Members;
import lombok.Getter;

import javax.persistence.*;

@Getter
@MappedSuperclass
public abstract class Comments extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    protected Members user;

    protected String content;

}
