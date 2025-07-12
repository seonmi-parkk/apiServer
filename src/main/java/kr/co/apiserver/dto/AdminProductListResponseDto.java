package kr.co.apiserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminProductListResponseDto {
    private Long pno;
    private String pname;
    private String title;
    private String sellerEmail;
}
