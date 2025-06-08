package kr.co.apiserver.controller;

import kr.co.apiserver.domain.ProductStatus;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.dto.ProductModifyRequestDto;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.service.ProductService;
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
@RequestMapping("/products")
public class ProductController {

    private final CustomFileUtil fileUtil;
    private final ProductService productService;



    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable("fileName") String fileName) {
       return fileUtil.getFile(fileName);
    }

    //@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public PageResponseDto<ProductDto> productList(PageRequestDto pageRequestDto) {
        return productService.getList(pageRequestDto);
    }

    @PostMapping("/")
    public Map<String, Long> register(ProductDto productDto) {
        // 파일 업로드 처리
        List<MultipartFile> files = productDto.getFiles();
        List<String> uploadFilenames = fileUtil.saveFiles(files);
        productDto.setUploadedFileNames(uploadFilenames);

        productDto.setStatus(ProductStatus.PENDING);
        Long pno = productService.register(productDto);
        return  Map.of("result", pno);
    }

    @GetMapping("/{pno}")
    public ProductDto read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable Long pno, ProductModifyRequestDto requestDto) {

        //requestDto.setPno(pno);

        // 기존 productDto
        //ProductDto oldProductDto = productService.get(pno);

        // 새로 upload된 file
        List<MultipartFile> files = requestDto.getFiles();
        List<String> newUploadFileNames = fileUtil.saveFiles(files);

        // 기존 이미지 중 keep 할 file
        //List<String> uploadedFileNames = requestDto.getUploadedFileNames();

        if(newUploadFileNames != null && !newUploadFileNames.isEmpty()) {
            requestDto.getUploadedFileNames().addAll(newUploadFileNames);
        }

        fileUtil.deleteFiles(requestDto.getDeletedFileNames());

        productService.modify(requestDto);

//        List<String> oldFileNames = oldProductDto.getUploadedFileNames();
//        if(oldFileNames != null && !oldFileNames.isEmpty()) {
//            List<String> removeFileNames = oldFileNames.stream().filter(fileName -> !uploadedFileNames.contains(fileName)).toList();
//
//            fileUtil.deleteFiles(removeFileNames);
//        }

        return Map.of("result", "success");
    }

    @PatchMapping("/{pno}")
    public Map<String, String> remove(@PathVariable Long pno) {
        log.info("remove pno: " + pno);
        List<String> oldFileNames = productService.get(pno).getUploadedFileNames();

        productService.remove(pno);

        fileUtil.deleteFiles(oldFileNames);

        return Map.of("result", "success");
    }
}
