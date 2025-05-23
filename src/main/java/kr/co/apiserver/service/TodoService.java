package kr.co.apiserver.service;

import kr.co.apiserver.domain.Todo;
import kr.co.apiserver.dto.TodoDto;

public interface TodoService {

    TodoDto get(Long tno);

    default TodoDto entityToDto(Todo todo) {
        return TodoDto.builder()
                .tno(todo.getTno())
                .title(todo.getTitle())
                .content(todo.getContent())
                .complete(todo.isComplete())
                .dueDate(todo.getDueDate())
                .build();
    }

    default Todo dtoToEntity(TodoDto todoDto) {
        return Todo.builder()
                .tno(todoDto.getTno())
                .title(todoDto.getTitle())
                .content(todoDto.getContent())
                .complete(todoDto.isComplete())
                .dueDate(todoDto.getDueDate())
                .build();
    }

}
