package kr.co.apiserver.repository.search;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.apiserver.domain.QTodo;
import kr.co.apiserver.domain.Todo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TodoSearchImpl implements TodoSearch {

    private final JPAQueryFactory queryFactory;
    private static final QTodo todo = QTodo.todo;

    @Override
    public Page<Todo> search1(long id) {

        log.info("search1........");

        List<Todo> content = queryFactory
                .selectFrom(todo)
                .where(
                    todo.title.contains("1"),
                    hasLastData(id)
                )
                .orderBy(todo.tno.desc())
                .limit(10)
                .fetch();

        return null;
    }

    private Predicate hasLastData(Long id) {
        if (id == null) {
            return null;
        }

        return todo.tno.lt(id);
    }
}