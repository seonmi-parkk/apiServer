package kr.co.apiserver.repository;

import kr.co.apiserver.domain.Product;
import kr.co.apiserver.repository.search.ProductSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductSearch {

    @EntityGraph(attributePaths = "imageList")
    @Query("SELECT p FROM Product p WHERE p.pno = :pno")
    Optional<Product> selectOne(@Param("pno") Long pno);

    @Modifying
    @Query("update Product p set p.deleted = :deleted where p.pno = :pno")
    void updateToDelete(@Param("pno") Long pno, @Param("deleted") boolean deleted);

    @Query("select p,pi from Product p left join p.imageList pi where pi.ord = 0 and p.deleted = false")
    Page<Object[]> selectList(Pageable pageable);
}
