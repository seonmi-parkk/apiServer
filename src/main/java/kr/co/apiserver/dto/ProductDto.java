package kr.co.apiserver.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long pno;
    private String name;
    private int price;
    private String desc;
    private boolean deleted;

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>(); // 파일 업로드시

    @Builder.Default
    private List<String> uploadedFileNames = new ArrayList<>(); // 파일 조회시

}
