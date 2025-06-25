package kr.co.apiserver.service.payment;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.IdempotencyContextType;
import kr.co.apiserver.domain.emums.OrderStatus;
import kr.co.apiserver.dto.PaymentApproveRequestDto;
import kr.co.apiserver.repository.OrderRepository;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentStrategyResolver strategyResolver;
    private final IdempotencyService idempotencyService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Transactional
    public String approvePayment(String idempotencyKey,
                                 PaymentApproveRequestDto requestDto,
                                 User user
    ) {
        // idempotencyKey 확인
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }

        // 중복 요청 방지를 위한 idempotency key lock
        String lockKey = "PAYMENT:" + idempotencyKey;
        if (!idempotencyService.tryLock(lockKey)) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
        }

        try {
            // redis에서 idempotency로 중복 요청 확인
            String cachedUrl = idempotencyService.verifyIdempotencyKey(idempotencyKey, user.getEmail(), IdempotencyContextType.PAYMENT_APPROVE);

            // 중복인 경우 이전 결과 url 반환
            if (cachedUrl != null) {
                return cachedUrl;
            } else {
                // 최초 저장
                idempotencyService.saveInitial(idempotencyKey, user.getEmail(), IdempotencyContextType.PAYMENT_APPROVE);
            }

            Orders order = orderRepository.findById(requestDto.getOrderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

            // 결제 전략 선택
            PaymentStrategy strategy = strategyResolver.resolve(order.getPaymentType().name());

            // 결제사별 API 호출 (결제 승인 요청)
            strategy.sendPaymentApprovalRequest(order, requestDto);

            /* 결제 성공 시 처리 */
            // 결제 승인 결과 URL
            String resultUrl = frontendBaseUrl + "/orders/detail?orderId=" + order.getOno();

            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(LocalDateTime.now());

            // Redis에 결과 캐싱(Used로 변경)
            idempotencyService.markAsUsed(
                    idempotencyKey,
                    user.getEmail(),
                    IdempotencyContextType.PAYMENT_APPROVE,
                    resultUrl
            );

            return resultUrl;

        } finally {
            idempotencyService.unlock(lockKey);
        }
    }

}
