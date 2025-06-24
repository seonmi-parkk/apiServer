package kr.co.apiserver.dto;

import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
public class OrderItemResponseDto {
    private Long pno;
    private String pname;
    private Integer price;
    private String imageFile;
}
