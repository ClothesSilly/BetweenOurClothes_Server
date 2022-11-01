package com.betweenourclothes.domain.stores;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name="sales_info_clothes")
public class SalesInfoClothes {
    @Id
    private Long id;
    private String clothes_brand;
    private String clothes_gender;
    private String clothes_size;
    private String clothes_color;
}
