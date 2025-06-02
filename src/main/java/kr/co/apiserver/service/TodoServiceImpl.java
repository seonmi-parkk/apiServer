package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Todo;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.TodoDto;
import kr.co.apiserver.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoDto get(Long tno) {
        Optional<Todo> result = todoRepository.findById(tno);
        Todo todo = result.orElseThrow();

        return entityToDto(todo);
    }

    @Override
    public Long register(TodoDto dto) {
        Todo todo = dtoToEntity(dto);
        Todo result = todoRepository.save(todo);
        return result.getTno();
    }

    @Transactional
    @Override
    public void modify(TodoDto dto) {
        Optional<Todo> result = todoRepository.findById(dto.getTno());

        Todo todo = result.orElseThrow();

        todo.changeTitle(dto.getTitle());
        todo.changeContent(dto.getContent());
        todo.changeComplete(dto.isComplete());
        todo.changeDueDate(dto.getDueDate());
    }

    @Override
    public void remove(Long tno) {
        todoRepository.deleteById(tno);
    }

    @Override
    public PageResponseDto<TodoDto> getList(PageRequestDto pageRequestDto) {
        Page<TodoDto> dtoList  = todoRepository.search(pageRequestDto, pageRequestDto.toPageable());
        return new PageResponseDto<>(dtoList);
    }

}
