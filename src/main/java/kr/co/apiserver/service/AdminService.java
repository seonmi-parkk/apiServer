package kr.co.apiserver.service;

import kr.co.apiserver.dto.AdminProductListResponseDto;
import kr.co.apiserver.response.ApiResponse;

public interface AdminService {
    ApiResponse<AdminProductListResponseDto> getProductApprovalRequestList();
}
