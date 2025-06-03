package kr.co.apiserver.dto;

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
    private String pname;
    private int price;
    private String imageFile;

    public CartItemListDto(long cino, String pname, int price, String imageFile) {
        this.cino = cino;
        this.pname = pname;
        this.price = price;
        this.imageFile = imageFile;
    }

}
