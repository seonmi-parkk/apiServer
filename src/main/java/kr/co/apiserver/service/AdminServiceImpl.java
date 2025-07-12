package kr.co.apiserver.service;

import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.AdminProductListResponseDto;
import kr.co.apiserver.repository.ProductRepository;
import kr.co.apiserver.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final ProductRepository productRepository;

    @Override
    public ApiResponse<AdminProductListResponseDto> getProductApprovalRequestList() {
        productRepository.findByStatus(ProductStatus.PENDING);
        return null;
    }
}
