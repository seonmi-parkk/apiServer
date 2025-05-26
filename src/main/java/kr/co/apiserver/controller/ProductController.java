package kr.co.apiserver.controller;

import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;

    @PostMapping("/")
    public Map<String, String> register(ProductDto productDto) {
        log.info("register: " + productDto);

        List<MultipartFile> files = productDto.getFiles();

        List<String> uploadedFileNames = fileUtil.saveFiles(files);

        productDto.setUploadedFileNames(uploadedFileNames);

        log.info(uploadedFileNames);

        return Map.of("result", "success");
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable("fileName") String fileName) {
       return fileUtil.getFile(fileName);
    }
}
