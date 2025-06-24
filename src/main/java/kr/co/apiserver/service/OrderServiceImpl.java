package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.*;
import kr.co.apiserver.dto.IdempotencyInfo;
import kr.co.apiserver.dto.OrderDetailResponseDto;
import kr.co.apiserver.dto.OrderPreviewResponseDto;
import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.repository.OrderRepository;
import kr.co.apiserver.repository.ProductRepository;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import kr.co.apiserver.service.payment.PaymentStrategy;
import kr.co.apiserver.service.payment.PaymentStrategyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IdempotencyKeyService idempotencyKeyService;
    private final PaymentStrategyResolver strategyResolver;


    @Transactional
    @Override
    public String createOrderAndRequestPayment(String idempotencyKey, OrderRequestDto dto, User user){

        log.info("dto.getIdempotencyKey(): {}", idempotencyKey);
        String lockKey = "PAYMENT:"+idempotencyKey;

        // idempotency key lock
        if (!idempotencyKeyService.tryLock(lockKey)) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
        }

        try {
            // Redis에서 먼저 중복 요청 여부 확인
            IdempotencyInfo idempotencyInfo = idempotencyKeyService.verifyIdempotencyKey(
                    idempotencyKey,
                    user.getEmail(),
                    IdempotencyContextType.PAYMENT
            );

            // 중복 요청의 경우 기존 URL 반환
            if (idempotencyInfo.getStatus() != PaymentIdempotencyStatus.READY) {
                String cachedRedirectUrl = idempotencyInfo.getResultUrl();
                log.info("redis cachedRedirectUrl: {}", cachedRedirectUrl);
                return cachedRedirectUrl;
            }

            // db에서 중복 확인
            Optional<Orders> existingOrder = orderRepository.findByIdempotencyKey(idempotencyKey);
            if (existingOrder.isPresent()) {
                String cachedRedirectUrl = existingOrder.get().getRedirectUrl();
                log.info("db cachedRedirectUrl: {}", cachedRedirectUrl);
                return cachedRedirectUrl;  // 중복 요청의 경우 기존 URL 반환
            }

            // 주문 생성
            Orders orders = Orders.builder()
                    .user(user)
                    .status(OrderStatus.WAITING_PAYMENT)
                    .idempotencyKey(idempotencyKey)
                    .paymentType(dto.getPaymentType())
                    .build();

            // 주문 상품 추가
            dto.getProductNos().forEach(productId -> {
                // 상품이 판매 중인 경우에만 주문 가능
                Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
                // 결제 페이지 요청 후 리다이렉트 URL 받기
                if (product.getStatus() != ProductStatus.APPROVED) {
                    throw new CustomException(ErrorCode.PURCHASE_NOT_AVAILABLE);
                }
                orders.addItem(product);
            });

            // 주문 저장
            Orders savedOrder = orderRepository.save(orders);

            // 결제 페이지 요청 후 리다이렉트 URL 받기
            PaymentStrategy strategy = strategyResolver.resolve(dto.getPaymentType().name());
            String redirectUrl = strategy.requestPaymentUrl(orders);
            savedOrder.setRedirectUrl(redirectUrl);

            // Redis에 결과 캐싱 (Used로 변경)
            idempotencyKeyService.markAsUsed(
                    idempotencyKey,
                    user.getEmail(),
                    IdempotencyContextType.PAYMENT,
                    redirectUrl
            );

            log.info("결재 요청 처리 완료 : {}", redirectUrl);
            return redirectUrl;
        } finally {
            // lock 해제
            idempotencyKeyService.unlock(lockKey);
        }

    }

    @Override
    public List<OrderPreviewResponseDto> previewOrder(OrderRequestDto dto) {
        // 상품 정보 조회
        return productRepository.getOrderPrivewInfo(dto.getProductNos())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public OrderDetailResponseDto getOrderDetail(Long ono, User user) {
        return null;
    }
}
