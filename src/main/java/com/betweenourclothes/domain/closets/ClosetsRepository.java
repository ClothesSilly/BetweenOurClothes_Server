package com.betweenourclothes.domain.closets;

import com.betweenourclothes.domain.clothes.ClothesImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ClosetsRepository extends JpaRepository<Closets, Long> {



    /*
    @Query(value =
            "select i " +
                    "from Closets c join c.images i on c.author.id = :member"
                    + " where index(i) = 0"
                    + " order by c.createdDate asc"
    )
    List<ClothesImage> findImagesByMemberIdOrderByCreatedDateDesc(@Param("member") Long id);
    */


}
