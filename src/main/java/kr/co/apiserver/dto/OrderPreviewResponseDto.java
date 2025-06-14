package kr.co.apiserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderPreviewResponseDto {
    private long pno;
    private String pname;
    private int price;
    private String imageFile;
}
