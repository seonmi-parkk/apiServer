package kr.co.apiserver.dto;

import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.ProductImage;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.ProductStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponseDto {

    private Long pno;
    private String sellerEmail;
    private String pname;
    private int price;

    public static ProductListResponseDto fromEntity(Product product) {
        return ProductListResponseDto.builder()
                .pno(product.getPno())
                .sellerEmail(product.getSeller().getEmail())
                .pname(product.getPname())
                .price(product.getPrice())
                .uploadedFileNames(product.getImageList().stream()
                        .map(ProductImage::getFileName)
                        .toList())
                .build();
    }

    private List<String> uploadedFileNames = new ArrayList<>(); // 파일 조회시

}
