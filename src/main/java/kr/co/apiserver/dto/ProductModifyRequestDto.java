package kr.co.apiserver.dto;

import kr.co.apiserver.domain.emums.ProductStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductModifyRequestDto {

    private Long pno;
    private String pname;
    private int price;
    private String pdesc;
    private List<ImageModifyRequestDto> images = new ArrayList<>();
    private List<Long> productCategories = new ArrayList<>();

}
