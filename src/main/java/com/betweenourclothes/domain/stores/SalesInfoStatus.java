package com.betweenourclothes.domain.stores;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name="sales_info_status")
public class SalesInfoStatus {

    @Id
    private Long id;
    private String status_tag;
    private String status_score;
    private String status_times;
    private String status_when2buy;
    private String transport;
}
