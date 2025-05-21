package kr.co.apiserver.repository.search;

import kr.co.apiserver.domain.Todo;
import org.springframework.data.domain.Page;

public interface TodoSearch {

    Page<Todo> search1();
}
