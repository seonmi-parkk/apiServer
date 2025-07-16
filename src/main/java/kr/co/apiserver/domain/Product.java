package kr.co.apiserver.domain;

import jakarta.persistence.*;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name="product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"imageList", "seller"})
public class Product extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_email", nullable = false)
    private User seller;

    @Column(nullable = false)
    private String pname;

    @Column(nullable = false)
    private int price;

    private String pdesc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.PENDING;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductCategory> productCategories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ord asc")
    @Column(nullable = false)
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    private int salesCount;

    @Version
    private Long version = 0L;

    public static Product createProduct(ProductDto productDto) {
        Product product = Product.builder()
                .pno(productDto.getPno())
                .seller(productDto.getSeller())
                .pname(productDto.getPname())
                .price(productDto.getPrice())
                .pdesc(productDto.getPdesc())
                .status(productDto.getStatus())
                .build();

        List<String> uploadedFileNames = productDto.getUploadedFileNames();
        if(uploadedFileNames == null || uploadedFileNames.isEmpty()) {
            return product;
        }

        uploadedFileNames.forEach(fileName -> {
            product.addImageString(fileName);
        });

        return product;
    }

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeDesc(String pdesc) {
        this.pdesc = pdesc;
    }

    public void changeName(String name) {
        this.pname = name;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    public void increaseSalesCount() {this.salesCount++;}

    public void addImage(ProductImage image) {
        image.setProduct(this);
        imageList.add(image);
    }

    public void addImageString(String fileName) {
        // 상품수정시 기존파일 중 하나만 바꿀때 기존 것들의 이름을 문자열로 같이 저장하는 처리를 하기 위해서
        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .ord(imageList.size())
                .build();

        addImage(productImage);
    }

    public void removeImage(ProductImage image) {
        this.imageList.remove(image);
        image.setProduct(null); // 연관관계 끊기
    }

    public void addCategory(Category category) {
        // 카테고리 수 최대 5개 제한
        if (this.productCategories.size() > 5) {
            throw new CustomException(ErrorCode.CATEGORY_LIMIT_EXCEEDED);
        }

        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(this);
        productCategory.setCategory(category);
        this.productCategories.add(productCategory);
    }

    public void clearCategories() {
        this.productCategories.clear();
    }
}
