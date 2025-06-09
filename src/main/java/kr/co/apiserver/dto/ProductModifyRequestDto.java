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
    private ProductStatus status = ProductStatus.APPROVED;

    private List<MultipartFile> files = new ArrayList<>(); // 파일 업로드시

    private List<String> uploadedFileNames = new ArrayList<>();

    private List<String> deletedFileNames = new ArrayList<>();

}
