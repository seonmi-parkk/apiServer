package kr.co.apiserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
@ToString
public class PageResponseDto<E> {

    private List<E> dtoList;

    private List<Integer> pageNumList;

    private boolean prev, next, isFirst, isLast;

    private int totalCount, prevPage, nextPage, totalPage, currentPage;

    public PageResponseDto(Page<E> pageDto) {

        this.dtoList = pageDto.getContent();
        this.totalCount = (int) pageDto.getTotalElements();
        this.totalPage = (int) pageDto.getTotalPages();
        this.currentPage = pageDto.getNumber() + 1;
        this.isFirst = pageDto.isFirst();
        this.isLast = pageDto.isLast();

        // 끝 페이지
        int end = (int) (Math.ceil(currentPage / 10.0)) * 10 ;
        // 시작 페이지
        int start = end - 9;

        end = end > totalPage ? totalPage : end;

        this.prev = start > 1;
        this.next = totalCount > end * pageDto.getSize();
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        this.prevPage = prev ? start - 1 : 0;
        this.nextPage = next ? end + 1 : 0;
    }

}
