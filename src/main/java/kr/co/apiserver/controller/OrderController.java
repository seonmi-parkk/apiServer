package kr.co.apiserver.controller;

import kr.co.apiserver.dto.OrderDetailResponseDto;
import kr.co.apiserver.dto.OrderPreviewResponseDto;
import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.IdempotencyService;
import kr.co.apiserver.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final IdempotencyService idempotencyService;

    // 주문 상품 리스트 (pno -> 상품 정보)
    @PostMapping("/preview")
    public ApiResponse<List<OrderPreviewResponseDto>> previewOrder(@RequestBody OrderRequestDto dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(orderService.previewOrder(dto));
    }

    // 주문 생성 및 결재 요청
    @PostMapping
    public ApiResponse<Map<String, String>> requestPayment(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody OrderRequestDto dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String paymentUrl = orderService.createOrderAndRequestPayment(idempotencyKey, dto, userDetails.getUser());
        return ApiResponse.ok(Map.of("paymentUrl",paymentUrl));
    }

    // 주문 상세페이지 정보
    @GetMapping("/{ono}")
    public ApiResponse<OrderDetailResponseDto> getOrderDetail(@PathVariable Long ono, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderDetailResponseDto orderDetail = orderService.getOrderDetail(ono, userDetails.getUser());
        return ApiResponse.ok(orderDetail);
    }


}
