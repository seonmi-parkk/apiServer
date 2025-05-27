package kr.co.apiserver.service;

import kr.co.apiserver.domain.Product;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.dto.TodoDto;
import kr.co.apiserver.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
}
