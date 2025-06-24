package kr.co.apiserver.service.payment;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.IdempotencyContextType;
import kr.co.apiserver.domain.emums.OrderStatus;
import kr.co.apiserver.domain.emums.PaymentIdempotencyStatus;
import kr.co.apiserver.dto.IdempotencyInfo;
import kr.co.apiserver.dto.PaymentApproveRequestDto;
import kr.co.apiserver.repository.OrderRepository;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.service.IdempotencyKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public abstract class AbstractPaymentStrategy implements PaymentStrategy {

    protected final OrderRepository orderRepository;
    protected final IdempotencyKeyService idempotencyKeyService;

    @Value("${frontend.base-url}")
    protected String frontendBaseUrl;

    protected AbstractPaymentStrategy(OrderRepository orderRepository, IdempotencyKeyService idempotencyKeyService) {
        this.orderRepository = orderRepository;
        this.idempotencyKeyService = idempotencyKeyService;
    }

    @Transactional
    @Override
    public String requestPaymentUrl(Orders orders) {
        String redirectUrl = sendPaymentUrlRequest(orders);

        orders.setRedirectUrl(redirectUrl);
        log.info("redirectUrl : {}", redirectUrl);
        return redirectUrl;
    }

    @Transactional
    @Override
    public String approvePayment(String idempotencyKey, PaymentApproveRequestDto requestDto, User user) {
        String lockKey = "PAYMENT:" + idempotencyKey;

        // idempotency key lock
        if (!idempotencyKeyService.tryLock(lockKey)) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
        }

        try {
            Orders order = orderRepository.findById(requestDto.getOrderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

            // 요청 검증 및 중복 확인
            validatePaymentApproveRequest(idempotencyKey, requestDto, user, order);

            // 결제사별 API 호출
            sendPaymentApprovalRequest(order, requestDto);

            /* 결제 성공 시 처리 */
            // 결제 승인 결과 URL
            String resultUrl = frontendBaseUrl + "/orders/detail?orderId=" + order.getOno();

            order.setStatus(OrderStatus.PAID);
            order.setRedirectUrl(resultUrl);

            // Redis에 결과 캐싱(Used로 변경)
            idempotencyKeyService.markAsPaid(
                    idempotencyKey,
                    user.getEmail(),
                    IdempotencyContextType.PAYMENT,
                    resultUrl
            );

            return resultUrl;

        } finally {
            idempotencyKeyService.unlock(lockKey);
        }
    }

    // 결제 승인 요청을 결제사별로 위임하는 메서드
    protected abstract void sendPaymentApprovalRequest(Orders order, PaymentApproveRequestDto requestDto);

    // 결제 URL 요청을 결제사별로 위임하는 메서드 (결제 페이지 URL 리턴)
    protected abstract String sendPaymentUrlRequest(Orders order);

    // 결제 승인 요청 검증 및 중복 체크
    private String validatePaymentApproveRequest(
            String idempotencyKey,
            PaymentApproveRequestDto requestDto,
            User user,
            Orders order
    ) {
        // Redis에서 먼저 중복 요청 여부 확인
        IdempotencyInfo idempotencyInfo = idempotencyKeyService.verifyIdempotencyKey(
                idempotencyKey,
                user.getEmail(),
                IdempotencyContextType.PAYMENT
        );

        // 중복 요청의 경우 기존 결과 반환
        if (idempotencyInfo.getStatus() == PaymentIdempotencyStatus.PAID) {
            String cachedResultUrl = idempotencyInfo.getResultUrl();
            log.info("cachedResultUrl: {}", cachedResultUrl);
            return cachedResultUrl;
        }


        if(!user.getEmail().equals(order.getUser().getEmail())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // db에서 중복 요청 확인
        if (order.getStatus() == OrderStatus.PAID) {
            String cachedResultUrl = order.getRedirectUrl();
            log.info("db cachedResultUrl: {}", cachedResultUrl);
            return cachedResultUrl;  // 중복 요청의 경우 기존 URL 반환
        }

        return null;
    }

}
