package kr.co.apiserver.service;

import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.TodoDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
@Log4j2
public class TodoServiceTest {

    @Autowired
    TodoService todoService;

    @Test
    public void testGet() {
        Long tno = 1L;

        TodoDto todoDto = todoService.get(tno);

        log.info(todoDto);
    }

    @Test
    public void testRegister() {
        TodoDto todoDto = TodoDto.builder()
                .title("Title")
                .userId(2L)
                .content("Content")
                .dueDate(LocalDate.now())
                .build();

        log.info(todoService.register(todoDto));
    }

    @Test
    public void testModify() {
        TodoDto todoDto = TodoDto.builder()
                .tno(6L)
                .title("Title Modified")
                .content("Content Modified")
                .dueDate(LocalDate.now())
                .build();

        todoService.modify(todoDto);
    }

    @Test
    public void testGetList() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().page(3).build();

        log.info(todoService.getList(pageRequestDto));
    }
}
