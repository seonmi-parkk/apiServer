package kr.co.apiserver.service;

import kr.co.apiserver.dto.AdminProductResponseDto;

import java.util.List;

public interface AdminService {
    List<AdminProductResponseDto> getProductApprovalRequestList();
}
