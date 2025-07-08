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

    /**
     * 결제 승인 요청
     * @param idempotencyKey 중복 요청 방지를 위한 키
     * @param requestDto 결제 승인 요청 DTO
     * @param userDetails 사용자 정보
     * @return 결제 승인 결과 URL
     */
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
