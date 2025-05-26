package kr.co.apiserver.repository.search;

import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;

public interface ProductSearch {

    PageResponseDto<ProductDto> searchList(PageRequestDto pageRequestDto);
}
