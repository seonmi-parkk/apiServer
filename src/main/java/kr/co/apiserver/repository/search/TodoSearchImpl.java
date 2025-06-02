package kr.co.apiserver.repository.search;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.apiserver.util.QueryDslUtil;
import kr.co.apiserver.domain.QTodo;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.TodoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TodoSearchImpl implements TodoSearch {

    private final JPAQueryFactory queryFactory;
    private static final QTodo todo = QTodo.todo;

    @Override
    public Page<TodoDto> search(PageRequestDto pageRequestDto, Pageable pageable) {
        List<TodoDto> result = queryFactory
            .select(
                Projections.fields(TodoDto.class,
                    todo.tno,
                    todo.title,
                    todo.dueDate
                )
            )
            .from(todo)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(QueryDslUtil.toOrderSpecifier(pageable.getSort(), todo))
            .fetch();

        JPAQuery<Long> totalCount = queryFactory
            .select(todo.count())
            .from(todo);

        return PageableExecutionUtils.getPage(result, pageable, totalCount::fetchOne);
    }

}