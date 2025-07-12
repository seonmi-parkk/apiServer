package kr.co.apiserver.controller;

import kr.co.apiserver.dto.AdminProductListResponseDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/products/approval-requests")
    public ApiResponse<AdminProductListResponseDto> getProductApprovalRequestList(){
        return adminService.getProductApprovalRequestList();
    }
}
