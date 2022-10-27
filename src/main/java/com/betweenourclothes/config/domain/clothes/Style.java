package com.betweenourclothes.config.domain.clothes;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@Entity
public class Style implements Serializable {

    @Id
    private int id;

    private String name;
}
