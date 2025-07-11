package kr.co.apiserver.controller;

import kr.co.apiserver.dto.CategoryResponseDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public ApiResponse<List<CategoryResponseDto>> getAllCategories() {
        return ApiResponse.ok(categoryService.getAllCategories());
    }
}
