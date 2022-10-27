package com.betweenourclothes.domain.clothes;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@Entity
@Table(name="clothes_info")
public class ClothesInfo implements Serializable {

    @Id
    private Long id;
    private String categoryL;
    private String categoryS;
    private String length;
    private String fit;
}
