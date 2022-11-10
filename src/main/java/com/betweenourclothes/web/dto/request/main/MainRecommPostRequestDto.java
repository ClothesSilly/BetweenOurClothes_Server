package com.betweenourclothes.web.dto.request.main;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.main.Recomm;
import com.betweenourclothes.domain.stores.Stores;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainRecommPostRequestDto {
    private List<Long> stores_id;

    public Recomm toEntity(Stores store, Closets closet){
        return Recomm.builder().store(store).closet(closet).build();
    }
}
