package kr.co.apiserver.controller;

import kr.co.apiserver.dto.PaymentApproveRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결재 승인
    @PutMapping("/approve")
    public ApiResponse<String> approvePayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PaymentApproveRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
       String resultUrl = paymentService.approvePayment(idempotencyKey, requestDto, userDetails.getUser());
         return ApiResponse.ok(resultUrl);
    }

}
