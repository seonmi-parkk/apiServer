package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.dto.*;

@Transactional
public interface ProductService {

    PageResponseDto<ProductListResponseDto> getList(PageRequestDto pageRequestDto);

    Long register(ProductDto productDto);

    ProductResponseDto get(Long pno);

    void modify(ProductModifyRequestDto requestDto);

    void remove(Long pno);
}
