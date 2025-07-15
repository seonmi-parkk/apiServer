package kr.co.apiserver.controller;

import kr.co.apiserver.domain.ProductImage;
import kr.co.apiserver.domain.emums.FileCategory;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.*;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.ProductService;
import kr.co.apiserver.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final CustomFileUtil fileUtil;
    private final ProductService productService;


    // 파일 조회
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable("fileName") String fileName) {
       return fileUtil.getFile(fileName);
    }

    // 상품 목록 조회
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public ApiResponse<PageResponseDto<ProductListResponseDto>> productList(PageRequestDto pageRequestDto) {
        return ApiResponse.ok(productService.getList(pageRequestDto));
    }

    // 상품 등록
    @PostMapping("/")
    public ApiResponse<Map<String, Long>> register(@AuthenticationPrincipal UserDetailsImpl userDetails, ProductDto productDto) {
        // 파일 업로드 처리
        List<MultipartFile> files = productDto.getFiles();
        List<String> uploadFilenames = fileUtil.saveFiles(files, FileCategory.PRODUCT);
        productDto.setUploadedFileNames(uploadFilenames);

        productDto.setStatus(ProductStatus.PENDING);
        productDto.setSeller(userDetails.getUser());

        Long pno = productService.register(productDto);
        return ApiResponse.ok(Map.of("result", pno));
    }

    // 상품 상세 조회
    @GetMapping("/{pno}")
    public ApiResponse<ProductResponseDto> read(@PathVariable("pno") Long pno) {
        return ApiResponse.ok(productService.getProductDetail(pno));
    }

    // 상품 수정
    @PutMapping("/{pno}")
    public ApiResponse<Map<String, String>> modify(@PathVariable Long pno, ProductModifyRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.modify(pno, requestDto, userDetails.getUsername());

//        List<String> oldFileNames = oldProductDto.getUploadedFileNames();
//        if(oldFileNames != null && !oldFileNames.isEmpty()) {
//            List<String> removeFileNames = oldFileNames.stream().filter(fileName -> !uploadedFileNames.contains(fileName)).toList();
//
//            fileUtil.deleteFiles(removeFileNames);
//        }

        return ApiResponse.ok(null);
    }
// 상품 수정
//    @PutMapping("/{pno}")
//    public ApiResponse<Map<String, String>> modify(@PathVariable Long pno, ProductModifyRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        // 승인 완료 된 상품은 수정 불가
//        ProductDto productDto = productService.findById(pno);
//        if(!productDto.getStatus().equals(ProductStatus.PENDING)) {
//            throw new CustomException(ErrorCode.BAD_REQUEST);
//        }
//
//        // 작성자만 수정 가능
//        if(!productDto.getSeller().getEmail().equals(userDetails.getUser().getEmail())) {
//            log.info("작성자: {}, 요청자: {}", productDto.getSeller().getEmail(), userDetails.getUsername());
//            throw new CustomException(ErrorCode.FORBIDDEN);
//        }
//
//        // 새로 upload된 file 처리
//        List<MultipartFile> files = requestDto.getFiles();
//        List<String> newUploadFileNames = fileUtil.saveFiles(files,"product");
//
//        // 기존 이미지 중 keep 할 file
//        //List<String> uploadedFileNames = requestDto.getUploadedFileNames();
//
//        if(newUploadFileNames != null && !newUploadFileNames.isEmpty()) {
//            requestDto.getFileNames().addAll(newUploadFileNames);
//        }
//
//        fileUtil.deleteFiles(requestDto.getDeletedFileNames());
//
//        productService.modify(requestDto);
//
////        List<String> oldFileNames = oldProductDto.getUploadedFileNames();
////        if(oldFileNames != null && !oldFileNames.isEmpty()) {
////            List<String> removeFileNames = oldFileNames.stream().filter(fileName -> !uploadedFileNames.contains(fileName)).toList();
////
////            fileUtil.deleteFiles(removeFileNames);
////        }
//
//        return ApiResponse.ok(null);
//    }

    // 상품 삭제
    @PatchMapping("/{pno}")
    public ApiResponse<Map<String, String>> remove(@PathVariable Long pno) {
        List<String> oldFileNames = productService.getProductDetail(pno).getUploadedFileNames();

        productService.remove(pno);

        fileUtil.deleteFiles(oldFileNames, FileCategory.PRODUCT);

        return ApiResponse.ok(null);
    }

    // 상품 판매 중지
    @PatchMapping("/{pno}/paused")
    public ApiResponse<Void> changeStatusToPaused(@PathVariable Long pno) {
        productService.changeStatusToPaused(pno);
        return ApiResponse.ok(null);
    }

    // 상품 판매 재개
    @PatchMapping("/{pno}/activated")
    public ApiResponse<Void> changeStatusToActivated(@PathVariable Long pno) {
        productService.changeStatusToActivated(pno);
        return ApiResponse.ok(null);
    }

    // 상품 승인
    @PatchMapping("/{pno}/approved")
    public ApiResponse<Void> changeStatusToApproved(@PathVariable Long pno) {
        productService.changeStatusToApproved(pno);
        return ApiResponse.ok(null);
    }

    // 상품 반려
    @PatchMapping("/{pno}/rejected")
    public ApiResponse<Void> changeStatusToRejected(@PathVariable Long pno) {
        productService.changeStatusToRejected(pno);
        return ApiResponse.ok(null);
    }
}
