package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.ProductStatus;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.dto.ProductModifyRequestDto;
import kr.co.apiserver.repository.ProductImageRepository;
import kr.co.apiserver.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public PageResponseDto<ProductDto> getList(PageRequestDto pageRequestDto) {
        log.info("getPage : "+pageRequestDto.getPage());
        log.info("getSize : "+pageRequestDto.getSize());
        // JPA
        Page<ProductDto> dtoList  = productRepository.searchList(pageRequestDto, pageRequestDto.toPageable());

        return new PageResponseDto<>(dtoList);
//        Pageable pageable = PageRequest.of(
//                pageRequestDto.getPage()-1,
//                pageRequestDto.getSize(),
//                Sort.by("pno").descending()
//        );
//
//        Page<Object[]> result = productRepository.selectList(pageable);
//
//        List<ProductDto> dtoList = result.get().map(arr -> {
//            ProductDto productDto = new ProductDto();
//            return ProductDto;
//        }).collect(Collectors.toList());
//
//        long totalCount = result.getTotalElements();
//
//        return PageResponseDto.<ProductDto>withAll()
//                .dtoList(dtoList)
//                .totalCount(totalCount)
//                .pageRequestDto(pageRequestDto)
//                .build();
//        return null;
    }

    @Transactional
    @Override
    public Long register(ProductDto productDto) {
        Product product = Product.createProduct(productDto);
        //log.info("==============product{} // imageList{} : ",product, product.getImageList());
        Long pno = productRepository.save(product).getPno();
        log.info("register pno : "+pno);
        return pno;
    }

    @Override
    public ProductDto get(Long pno) {
        Optional<Product> result = productRepository.findByIdWithImages(pno);

        Product product = result.orElseThrow();

        log.info("==============product{} // imageList{} : ",product, product.getImageList());

        return ProductDto.fromEntity(product);
    }

    @Transactional
    @Override
    public void modify(ProductModifyRequestDto requestDto) {
        // 삭제된 이미지 제거
        int deleteResult = productImageRepository.deleteAllByFileNameInBatch(requestDto.getDeletedFileNames());
        log.info("deleteResult : "+deleteResult);

        // 조회
        Optional<Product> result = productRepository.findById(requestDto.getPno());
        Product product = result.orElseThrow();

        // 변경내용 반영
        product.changePrice(requestDto.getPrice());
        product.changeName(requestDto.getPname());
        product.changeDesc(requestDto.getPdesc());
        product.changeStatus(requestDto.getStatus());

        // 새로운 이미지 추가
        List<String> uploadFileNames = requestDto.getUploadedFileNames();
        // product.clearList();
        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(fileName -> {
                product.addImageString(fileName);
            });
        }

    }

    @Transactional
    @Override
    public void remove(Long pno) {
        Product product = productRepository.findById(pno).orElseThrow();
        product.changeStatus(ProductStatus.DELETED);
    }
}
