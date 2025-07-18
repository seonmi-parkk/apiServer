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
public class ProductResponseDto {

    private Long pno;
    private String pname;
    private int price;
    private String pdesc;
    private String status;
    private String statusName;
    private String sellerEmail;
    private String sellerNickname;
    private String sellerImage;
    private List<String> uploadedFileNames = new ArrayList<>();
    private List<String> productCategories = new ArrayList<>();


    public static ProductResponseDto fromEntity(Product product) {
        return ProductResponseDto.builder()
                .pno(product.getPno())
                .sellerEmail(product.getSeller().getEmail())
                .sellerNickname(product.getSeller().getNickname())
                .sellerImage(product.getSeller().getProfileImage())
                .pname(product.getPname())
                .price(product.getPrice())
                .pdesc(product.getPdesc())
                .status(product.getStatus().name())
                .statusName(product.getStatus().getMessage())
                .uploadedFileNames(product.getImageList().stream()
                        .map(ProductImage::getFileName)
                        .toList())
                .productCategories(product.getProductCategories().stream()
                        .map(category -> category.getCategory().getName())
                        .toList())
                .build();
    }

}
