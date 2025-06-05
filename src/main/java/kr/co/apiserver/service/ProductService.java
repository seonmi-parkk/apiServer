package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.dto.ProductModifyRequestDto;

@Transactional
public interface ProductService {

    PageResponseDto<ProductDto> getList(PageRequestDto pageRequestDto);

    Long register(ProductDto productDto);

    ProductDto get(Long pno);

    void modify(ProductModifyRequestDto requestDto);

    void remove(Long pno);
}
