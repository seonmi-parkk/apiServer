package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.*;

@Transactional
public interface ProductService {

    PageResponseDto<ProductListResponseDto> getList(PageRequestDto pageRequestDto);

    Long register(ProductDto productDto);

    ProductResponseDto getProductDetail(Long pno);

    void modify(Long pno, ProductModifyRequestDto requestDto, String userEmail);

    void remove(Long pno);

    void changeStatusToPaused(Long pno);

    void changeStatusToActivated(Long pno);

}
