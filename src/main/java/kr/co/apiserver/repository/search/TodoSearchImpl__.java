//package kr.co.apiserver.repository.search;
//
//import com.querydsl.jpa.JPQLQuery;
//import kr.co.apiserver.domain.QTodo;
//import kr.co.apiserver.domain.Todo;
//import kr.co.apiserver.dto.PageRequestDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.*;
//import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
//
//import java.util.List;
//
//@Slf4j
//public class TodoSearchImpl__ extends QuerydslRepositorySupport implements TodoSearch {
//
//    public TodoSearchImpl__() {
//        super(Todo.class);
//    }
//
//    @Override
//    public Page<Todo> search1(PageRequestDto pageRequestDto) {
//
//        log.info("search1........");
//
//        QTodo todo = QTodo.todo;
//
//        JPQLQuery<Todo> query = from(todo);
//
//        Pageable pageable = PageRequest.of(
//                pageRequestDto.getPage(),
//                pageRequestDto.getSize(),
//                Sort.by("tno").descending()
//        );
//
//        this.getQuerydsl().applyPagination(pageable, query);
//
//        List<Todo> list = query.fetch(); // 목록 데이터
//
//        long total = query.fetchCount();
//
//        return new PageImpl<>(list, pageable, total);
//    }
//}
