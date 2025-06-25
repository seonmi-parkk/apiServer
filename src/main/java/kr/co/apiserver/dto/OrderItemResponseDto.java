package kr.co.apiserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemResponseDto {
    private Long pno;
    private String pname;
    private Integer price;
    private String imageFile;

    public OrderItemResponseDto(Long pno, String pname, Integer price, String imageFile) {
        this.pno = pno;
        this.pname = pname;
        this.price = price;
        this.imageFile = imageFile;
    }
}
