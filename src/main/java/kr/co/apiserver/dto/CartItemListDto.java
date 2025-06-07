package kr.co.apiserver.dto;

import kr.co.apiserver.domain.ProductStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class CartItemListDto {

    private long cino;
    private long pno;
    private String pname;
    private int price;
    private ProductStatus status;
    private String imageFile;

    public CartItemListDto(long cino, long pno, String pname, int price, ProductStatus status, String imageFile) {
        this.cino = cino;
        this.pno = pno;
        this.pname = pname;
        this.price = price;
        this.status = status;
        this.imageFile = imageFile;
    }

}
