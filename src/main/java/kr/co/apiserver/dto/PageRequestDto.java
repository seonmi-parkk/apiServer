package kr.co.apiserver.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private Sort.Direction direction = Sort.Direction.DESC;

    @Builder.Default
    private String sortBy = "id";

    // 페이지 0 또는 음수일 경우 방어
    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    // 사이즈 0 또는 음수일 경우 방어
    public void setSize(int size) {
        this.size = Math.max(size, 1);
    }

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size, direction, sortBy);
    }


}
