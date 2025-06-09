package kr.co.apiserver.domain;

import jakarta.persistence.*;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.ProductDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"imageList", "seller"})
public class Product {

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
    // private boolean deleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.PENDING;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ord asc")
    @Column(nullable = false)
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

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
//        productDto.getFiles().forEach(file -> {
//            ProductImage image = ProductImage.builder()
//                    .fileName(file.getOriginalFilename())
//                    .build();
//            product.addImage(image);

//        });

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

//    public void changeDeleted(boolean deleted) {
//        this.deleted = deleted;
//    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

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

//    public void clearList() {
//        this.imageList.clear();
//    }
}
