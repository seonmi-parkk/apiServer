package kr.co.apiserver.dto;

import kr.co.apiserver.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryResponseDto {
    private Long cgno;
    private String name;

    public static CategoryResponseDto fromEntity(Category category) {
        return new CategoryResponseDto(category.getCgno(), category.getName());
    }

}
