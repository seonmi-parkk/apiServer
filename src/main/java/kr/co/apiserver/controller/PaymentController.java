package kr.co.apiserver.controller;

import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.emums.PaymentType;
import kr.co.apiserver.dto.PaymentApproveRequestDto;
import kr.co.apiserver.repository.OrderRepository;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.payment.PaymentStrategy;
import kr.co.apiserver.service.payment.PaymentStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentStrategyResolver strategyResolver;
    private final OrderRepository orderRepository;

    // 결재 승인
    @PutMapping("/approve")
    public ApiResponse<String> approvePayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PaymentApproveRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Orders order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 결제 전략 선택
        PaymentType paymentType = order.getPaymentType();
        PaymentStrategy strategy = strategyResolver.resolve(paymentType.name());

        String successUrl = strategy.approvePayment(idempotencyKey, requestDto, userDetails.getUser());
        return ApiResponse.ok(successUrl);
    }

}
