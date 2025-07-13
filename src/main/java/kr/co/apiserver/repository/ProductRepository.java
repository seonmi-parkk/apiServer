package kr.co.apiserver.repository;

import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.AdminProductResponseDto;
import kr.co.apiserver.dto.OrderPreviewResponseDto;
import kr.co.apiserver.repository.search.ProductSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductSearch {

    @Query("select p from Product p " +
            "join fetch p.seller " +
            "left join fetch p.imageList " +
            "where p.pno = :pno")
    Optional<Product> findByIdWithImagesAndProductCategories(Long pno);

    @EntityGraph(attributePaths = "imageList")
    @Query("SELECT p FROM Product p WHERE p.pno = :pno")
    Optional<Product> selectOne(@Param("pno") Long pno);


    @Query("select " +
            "new kr.co.apiserver.dto.OrderPreviewResponseDto(p.pno, p.pname, p.price, pi.fileName)" +
             "from Product p inner join ProductImage pi on p.pno = pi.product.pno " +
             " and pi.ord= 0 " +
            "where p.pno in :pnos")
    Optional<List<OrderPreviewResponseDto>> getOrderPrivewInfo(@Param("pnos") List<Long> productsNos);

    @Query("select " +
            "new kr.co.apiserver.dto.AdminProductResponseDto(p.pno, p.pname, p.price, p.createdAt, s.nickname, i.fileName) " +
            "from Product p " +
            "join p.seller s " +
            "left join p.imageList i " +
            "where p.status = :status " +
            "and i.ord = 0 " +
            "order by p.pno desc")
    List<AdminProductResponseDto> findByStatus(ProductStatus status);
}
