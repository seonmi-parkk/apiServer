package kr.co.apiserver.repository.search;

import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Log4j2
public class ProductSearchImpl implements ProductSearch {

    @Override
    public PageResponseDto<ProductDto> searchList(PageRequestDto pageRequestDto) {
//        Pageable pagable = PageRequest.of(pageRequestDto.getPage() -1,
//                pageRequestDto.getSize(),
//                Sort.by("pno").descending());
        return null;
    }
}
