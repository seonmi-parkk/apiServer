package kr.co.apiserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminProductResponseDto {
    private Long pno;
    private String pname;
    private Integer price;
    private LocalDate createdAt;
    private String sellerNickname;
    private String productImage;

    public AdminProductResponseDto(Long pno, String pname, Integer price, LocalDateTime createdAt, String sellerNickname, String productImage) {
        this.pno = pno;
        this.pname = pname;
        this.price = price;
        this.createdAt = createdAt != null ? createdAt.toLocalDate() : null;
        this.sellerNickname = sellerNickname;
        this.productImage = productImage;
    }

}