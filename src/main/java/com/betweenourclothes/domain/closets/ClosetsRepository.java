package com.betweenourclothes.domain.closets;

import com.betweenourclothes.domain.clothes.ClothesImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClosetsRepository extends JpaRepository<Closets, Long> {
    @Query(value =
            "select i " +
                    "from Closets c join c.images i on c.author.id = :member"
            + " where index(i) = 0"
            + " order by c.createdDate desc"
    )
    Page<ClothesImage> findImagesByMemberIdOrderByCreatedDateDesc(@Param("member") Long id, Pageable pageable);


    @Query(value =
            "select i " +
                    "from Closets c join c.images i on c.author.id = :member "
            + " where index(i) = 0 and c.clothesInfo.categoryL = :name"
            + " order by c.createdDate desc"
    )
    Page<ClothesImage> findImagesByCategoryLDesc(@Param("member") Long id, @Param("name") String category_l, Pageable pageable);


    @Query(value =
            "select i " +
                    "from Closets c join c.images i on c.author.id = :member "
                    + " where index(i) = 0 and c.clothesInfo.categoryL = :nameL" +
                    " and c.clothesInfo.categoryS = :nameS"
                    + " order by c.createdDate desc"
    )
    Page<ClothesImage> findImagesByCategoryLAndCategorySDesc(@Param("member")Long id, @Param("nameL")String nameL,
                                                             @Param("nameS")String nameS, Pageable pageable);



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
