package kr.co.apiserver.dto;

import kr.co.apiserver.domain.emums.SearchType;
import kr.co.apiserver.domain.emums.SortType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 12;

    @Builder.Default
    private Sort.Direction direction = Sort.Direction.DESC;

    @Builder.Default
    private SortType sortBy = SortType.LATEST;

    // 검색 조건
    private String keyword;
    private SearchType type;

    // 가격 필터 조건
    private Integer minPrice;
    private Integer maxPrice;

    // 카테고리
    private List<Long> categories;

    // 페이지 0 또는 음수일 경우 방어
    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    // 사이즈 0 또는 음수일 경우 방어
    public void setSize(int size) {
        this.size = Math.max(size, 1);
    }

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }


}
