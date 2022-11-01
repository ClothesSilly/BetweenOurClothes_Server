package com.betweenourclothes.domain.stores;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name="sales_info_user")
public class SalesInfoUser {

    @Id
    private Long id;
    private String user_size;
    private String user_weight;
    private String user_height;
}
