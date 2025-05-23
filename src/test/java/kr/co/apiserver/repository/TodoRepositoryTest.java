package kr.co.apiserver.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import kr.co.apiserver.domain.Todo;
import kr.co.apiserver.repository.search.TodoSearch;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class TodoRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void test1() {

        Assertions.assertNotNull(todoRepository);

        log.info(todoRepository.getClass().getName());
    }

    @Test
    public void testInsert(){

        Todo todo = Todo.builder()
                .title("Title")
                .content("content...")
                .dueDate(LocalDate.of(2025,05,21))
                .build();

        Todo result = todoRepository.save(todo);

        log.info(result);
    }

    @Test
    public void testRead(){

       Long tno = 1L;

       Optional<Todo> result = todoRepository.findById(tno);
       Todo todo = result.orElseThrow();
         log.info(todo);
    }

    @Test
    @Transactional
    public void testUpdate(){

        Long tno = 1L;
        Optional<Todo> result = todoRepository.findById(tno);
        Todo todo = result.orElseThrow();

        todo.setTitle("Update Title22");
        todo.setContent("Update Content22");
        todo.setComplete(true);

        // 변경 사항 확인용
        entityManager.flush();
    }

    @Test
    public void testPaging(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by("tno").descending());

        Page<Todo> result = todoRepository.findAll(pageable);

        log.info(result.getTotalElements());

        log.info(result.getContent());


    }


    @Test
    public void testSearch1(){
        todoRepository.search1(0L);
    }


}
