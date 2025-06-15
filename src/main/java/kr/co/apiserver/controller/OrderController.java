package kr.co.apiserver.controller;

import kr.co.apiserver.dto.CartItemListDto;
import kr.co.apiserver.dto.OrderPreviewResponseDto;
import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 상품 리스트 (pno -> 상품 정보)
    @PostMapping("/preview")
    public ApiResponse<List<OrderPreviewResponseDto>> previewOrder(@RequestBody OrderRequestDto dto) {
        List<OrderPreviewResponseDto> orderPreview = orderService.previewOrder(dto);
        return ApiResponse.ok(orderPreview);
    }

    // 주문 생성 및 결재 요청
    @PostMapping
    public ApiResponse<Map<String, String>> requestPayment(@RequestBody OrderRequestDto dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String paymentUrl = orderService.createOrderAndRequestPayment(dto, userDetails.getUser());
        return ApiResponse.ok(Map.of("paymentUrl",paymentUrl));
    }
}
