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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        todo.setTitle(dto.getTitle());
        todo.setContent(dto.getContent());
        todo.setComplete(dto.isComplete());
        todo.setDueDate(dto.getDueDate());

    }

    @Override
    public void remove(Long tno) {
        todoRepository.deleteById(tno);
    }

    @Override
    public PageResponseDto<TodoDto> getList(PageRequestDto pageRequestDto) {
        log.info("getPage : "+pageRequestDto.getPage());
        log.info("getSize : "+pageRequestDto.getSize());
        // JPA
        Page<TodoDto> dtoList  = todoRepository.search1(pageRequestDto, pageRequestDto.toPageable());

        PageResponseDto<TodoDto> responseDto =
                PageResponseDto.<TodoDto>withAll()
                        .dtoList(dtoList.getContent())
                        .pageRequestDto(pageRequestDto)
                        .totalElements(dtoList.getTotalElements())
                        .totalPage(dtoList.getTotalPages())
                        .currentPage(dtoList.getNumber())
                        .isLast(dtoList.isLast())
                        .isFirst(dtoList.isFirst())
                        .build();

        return responseDto;
    }

//    @Override
//    public PageResponseDto<TodoDto> getList(PageRequestDto pageRequestDto) {
//        log.info("getPage : "+pageRequestDto.getPage());
//        log.info("getSize : "+pageRequestDto.getSize());
//        // JPA
//        Page<Todo> result = todoRepository.search1(pageRequestDto);
//
//        List<TodoDto> dtoList = result.get()
//                .map(todo -> entityToDto(todo)).collect(Collectors.toList());
//
//        PageResponseDto<TodoDto> responseDto =
//                PageResponseDto.<TodoDto>withAll()
//                        .dtoList(dtoList)
//                        .pageRequestDto(pageRequestDto)
//                        .total(result.getTotalElements())
//                        .build();
//
//        return responseDto;
//    }

}
