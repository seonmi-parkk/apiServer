package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
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

    @Override
    public Long register(ProductDto productDto) {
        Product product = Product.createProduct(productDto);
        log.info("==============product{} // imageList{} : ",product, product.getImageList());
        Long pno = productRepository.save(product).getPno();
        return pno;
    }

    @Override
    public ProductDto get(Long pno) {
        Optional<Product> result = productRepository.findById(pno);

        Product product = result.orElseThrow();

        return ProductDto.fromEntity(product);
    }

    @Transactional
    @Override
    public void modify(ProductDto productDto) {
        // 조회
        Optional<Product> result = productRepository.findById(productDto.getPno());
        Product product = result.orElseThrow();

        // 변경내용 반영
        product.changePrice(productDto.getPrice());
        product.changeName(productDto.getPname());
        product.changeDesc(productDto.getPdesc());
        product.changeDeleted(productDto.isDeleted());

        // 이미지 처리
        List<String> uploadFileNames = productDto.getUploadedFileNames();

        product.clearList();

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
        product.changeDeleted(true);
    }
}
