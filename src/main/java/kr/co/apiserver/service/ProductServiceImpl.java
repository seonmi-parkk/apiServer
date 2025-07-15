package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Category;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.ProductImage;
import kr.co.apiserver.domain.emums.FileCategory;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.*;
import kr.co.apiserver.repository.CategoryRepository;
import kr.co.apiserver.repository.ProductRepository;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CustomFileUtil fileUtil;
    private final CategoryRepository categoryRepository;

    @Override
    public PageResponseDto<ProductListResponseDto> getList(PageRequestDto pageRequestDto) {

        // JPA
        Page<ProductListResponseDto> dtoList  = productRepository.searchList(pageRequestDto, pageRequestDto.toPageable());

        return new PageResponseDto<>(dtoList);
//
//
//
    }

    @Transactional
    @Override
    public Long register(ProductDto productDto) {
        Product product = Product.createProduct(productDto);

        categoryRepository.findAllById(productDto.getProductCategories())
                .forEach(category -> product.addCategory(category));

        Long pno = productRepository.save(product).getPno();

        return pno;
    }

    @Override
    public ProductResponseDto getProductDetail(Long pno) {
        Optional<Product> result = productRepository.findByIdWithImagesAndProductCategories(pno);

        Product product = result.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponseDto.fromEntity(product);
    }

    @Transactional
    @Override
    public void modify(Long pno, @ModelAttribute ProductModifyRequestDto requestDto, String userEmail) {
        // 상품 수정 전 검증
        Product product = validateWritable(pno, userEmail);

        // 기존 이미지 목록 가져오기
        List<ProductImage> oldImages = product.getImageList();
        Map<String, ProductImage> oldImageMap = new HashMap<>();
        oldImages.forEach(img -> oldImageMap.put(img.getFileName(), img));

        // 클라이언트에서 넘어온 이미지 이름
        Set<String> receivedFilenames = requestDto.getImages().stream()
                .map(img -> img.getFileName())
                .collect(Collectors.toSet());

        // 삭제 대상 이미지
        List<ProductImage> toDelete = oldImages.stream()
                .filter(img -> !receivedFilenames.contains(img.getFileName()))
                .toList();

        List<String> toDeleteNames = new ArrayList<>();

        // 삭제 대상 이미지 제거
        for (ProductImage image : toDelete) {
            product.removeImage(image);
            toDeleteNames.add(image.getFileName());
        }
        fileUtil.deleteFiles(toDeleteNames, FileCategory.PRODUCT);

        // 상품 변경내용 반영
        product.changePrice(requestDto.getPrice());
        product.changeName(requestDto.getPname());
        product.changeDesc(requestDto.getPdesc());

        // 상품 카테고리 변경
        product.clearCategories();
        List<Category> newCategories = categoryRepository.findAllById(requestDto.getProductCategories());
        newCategories.forEach(category -> product.addCategory(category));

        // 남은 이미지 처리
        int ord = 0;
        for (ImageModifyRequestDto dto : requestDto.getImages()) {
            MultipartFile file = dto.getFile();
            if ( file != null) {
                // 파일 저장
                String fileName = fileUtil.saveFile(file, FileCategory.PRODUCT);
                // 새 이미지- 새로 저장
                ProductImage newImage = new ProductImage();
                newImage.setFileName(fileName);
                newImage.setOrd(ord++);
                product.addImage(newImage);
            } else {
                // 기존 이미지- 순서 갱신
                ProductImage existingImage = oldImageMap.get(dto.getFileName());
                existingImage.setOrd(ord++);
            }
        }

        productRepository.save(product);

    }

    @Transactional
    @Override
    public void remove(Long pno) {
        Product product = productRepository.findById(pno).orElseThrow();
        product.changeStatus(ProductStatus.DELETED);
    }

    @Transactional
    @Override
    public void changeStatusToPaused(Long pno) {
        Product product = productRepository.findById(pno)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 판매 중인 상품의 경우에만 판매 중지 가능
        if (!product.getStatus().equals(ProductStatus.APPROVED)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        product.changeStatus(ProductStatus.PAUSED);
    }

    @Transactional
    @Override
    public void changeStatusToActivated(Long pno) {
        Product product = productRepository.findById(pno)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 판매 중지된 상품의 경우에만 판매 재개 가능
        if (!product.getStatus().equals(ProductStatus.PAUSED)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        product.changeStatus(ProductStatus.APPROVED);
    }

    @Transactional
    @Override
    public void changeStatusToApproved(Long pno) {
        Product product = productRepository.findById(pno)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 승인 대기 상태인 상품만 승인 가능
        if (!product.getStatus().equals(ProductStatus.PENDING)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        product.changeStatus(ProductStatus.APPROVED);
    }

    @Transactional
    @Override
    public void changeStatusToRejected(Long pno) {
        Product product = productRepository.findById(pno)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 승인 대기 상태인 상품만 반려 가능
        if (!product.getStatus().equals(ProductStatus.PENDING)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        product.changeStatus(ProductStatus.REJECTED);
    }

    // 상품 수정 전 검증
    private Product validateWritable(Long pno, String userEmail) {
        Product product = productRepository.findById(pno)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getStatus() != ProductStatus.PENDING) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        validateOwner(product, userEmail);
        return product;
    }

    // 판매 중지 전 검증
    private Product validateStopSale(Long pno, String userEmail) {
        Product product = getProduct(pno);
        if (product.getStatus() != ProductStatus.APPROVED) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        validateOwner(product, userEmail);
        return product;
    }

    // 판매 재개 전 검증
    private Product validateReopenSale(Long pno, String userEmail) {
        Product product = getProduct(pno);
        if (product.getStatus() != ProductStatus.PAUSED) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        validateOwner(product, userEmail);
        return product;
    }

    // 작성자 여부 검증
    private void validateOwner(Product product, String userEmail) {
        if (!product.getSeller().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    // 상품 조회
    private Product getProduct(Long pno) {
        return productRepository.findById(pno)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

}
