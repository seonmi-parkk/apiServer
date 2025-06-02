package kr.co.apiserver.repository.search;

import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.TodoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoSearch {
    Page<TodoDto> search(PageRequestDto pageRequestDto, Pageable pageable);
}
