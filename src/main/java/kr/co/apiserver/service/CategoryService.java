package kr.co.apiserver.service;


import kr.co.apiserver.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getAllCategories();
}
