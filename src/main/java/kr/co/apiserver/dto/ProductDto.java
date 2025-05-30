package kr.co.apiserver.dto;

import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.ProductImage;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long pno;
    private String name;
    private int price;
    private String pdesc;
    private boolean deleted;

    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .pno(product.getPno())
                .name(product.getName())
                .price(product.getPrice())
                .pdesc(product.getPdesc())
                .deleted(product.isDeleted())
                .uploadedFileNames(product.getImageList().stream()
                        .map(ProductImage::getFileName)
                        .toList())
                .build();
    }

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>(); // 파일 업로드시

    @Builder.Default
    private List<String> uploadedFileNames = new ArrayList<>(); // 파일 조회시

}
