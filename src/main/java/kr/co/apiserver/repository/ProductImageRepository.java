package kr.co.apiserver.repository;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.fileName IN :fileNames")
    int deleteAllByFileNameInBatch(@Param("fileNames") List<String> fileNames);

}
