package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Todo;
import kr.co.apiserver.dto.TodoDto;
import kr.co.apiserver.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoDto get(Long tno) {
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        return entityToDto(todo);
    }

}
