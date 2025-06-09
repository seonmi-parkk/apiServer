package kr.co.apiserver.repository.search;

import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.dto.ProductListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSearch {

    Page<ProductListResponseDto> searchList(PageRequestDto pageRequestDto, Pageable pageable);
}
