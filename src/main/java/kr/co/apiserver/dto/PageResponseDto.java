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

    //private PageRequestDto pageRequestDto;

    private boolean prev, next, isFirst, isLast;

    private int totalCount, prevPage, nextPage, totalPage, currentPage;

    public PageResponseDto(Page<E> pageDto) {

        this.dtoList = pageDto.getContent();
        this.totalCount = (int) pageDto.getTotalElements();
        this.totalPage = (int) pageDto.getTotalPages();
        this.currentPage = pageDto.getNumber() + 1;
        this.isFirst = pageDto.isFirst();
        this.isLast = pageDto.isLast();

        log.info("------------------totalPage가 총인지, 현재에서 총인지 :"+totalPage);
        log.info("------------------totalCount 총인지, 현재에서 총인지 :"+totalCount);

        // 끝 페이지
        int end = (int) (Math.ceil(currentPage / 10.0)) * 10 ;
        // 시작 페이지
        int start = end - 9;

        // 실제 마지막 페이지
       // int last = (int) (Math.ceil(totalCount/(double)pageDto.getSize()));

        end = end > totalPage ? totalPage : end;

        this.prev = start > 1;
        this.next = totalCount > end * pageDto.getSize();
        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        this.prevPage = prev ? start - 1 : 0;
        this.nextPage = next ? end + 1 : 0;
    }

//    @Builder(builderMethodName = "withAll")
//    public PageResponseDto(
//            List<E> dtoList,
//            PageRequestDto pageRequestDto,
//            long totalCount,
//            long totalPage,
//            int currentPage,
//            boolean isFirst,
//            boolean isLast
//    ) {
//
//        this.dtoList = dtoList;
//        //this.pageRequestDto = pageRequestDto;
//        this.totalCount = (int) totalCount;
//        this.totalPage = (int) totalPage;
//        this.currentPage = currentPage + 1;
//        this.isFirst = isFirst;
//        this.isLast = isLast;
//
//        // 끝 페이지
//        int end = (int) (Math.ceil(pageRequestDto.getPage() / 10.0)) * 10 ;
//        // 시작 페이지
//        int start = end - 9;
//
//        // 실제 마지막 페이지
//        int last = (int) (Math.ceil(totalCount/(double)pageRequestDto.getSize()));
//
//        end = end > last ? last : end;
//
//        this.prev = start > 1;
//        this.next = totalCount > end * pageRequestDto.getSize();
//        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
//        this.prevPage = prev ? start - 1 : 0;
//        this.nextPage = next ? end + 1 : 0;
//
//    }
}
