package kr.co.apiserver.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageModifyRequestDto {
    private String fileName;
    private MultipartFile file;
}
