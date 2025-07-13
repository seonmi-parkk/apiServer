package kr.co.apiserver.service;

import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.AdminProductResponseDto;
import kr.co.apiserver.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final ProductRepository productRepository;

    @Override
    public List<AdminProductResponseDto> getProductApprovalRequestList() {
        return productRepository.findByStatus(ProductStatus.PENDING);
    }
}
