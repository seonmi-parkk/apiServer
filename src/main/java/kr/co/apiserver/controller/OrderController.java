package kr.co.apiserver.controller;

import kr.co.apiserver.domain.emums.IdempotencyContextType;
import kr.co.apiserver.dto.IdempotencyInfo;
import kr.co.apiserver.dto.OrderDetailResponseDto;
import kr.co.apiserver.dto.OrderPreviewResponseDto;
import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.IdempotencyKeyService;
import kr.co.apiserver.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final IdempotencyKeyService idempotencyKeyService;

    // 주문 상품 리스트 (pno -> 상품 정보)
    @PostMapping("/preview")
    public ApiResponse<Map<String, Object>> previewOrder(@RequestBody OrderRequestDto dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // idempotencyKey 생성
        String idempotencyKey = UUID.randomUUID().toString();

        Map<String, Object> result = Map.of(
                "orderPreview", orderService.previewOrder(dto),
                "idempotencyKey", idempotencyKey
        );

        // Idempotency Key 저장
        idempotencyKeyService.saveInitial(idempotencyKey, userDetails.getUsername(), IdempotencyContextType.PAYMENT);

        return ApiResponse.ok(result);
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
