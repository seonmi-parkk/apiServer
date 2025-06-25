package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.*;
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
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IdempotencyService idempotencyService;

    private final PaymentStrategyResolver strategyResolver;

    /**
     * 주문 생성 및 결제 요청 처리
     * @param idempotencyKey 중복 요청 방지를 위한 키
     * @param dto            주문 요청 정보
     * @param user           주문을 요청한 사용자
     * @return 결제 페이지로 리다이렉트할 URL
     */
    @Transactional
    @Override
    public String createOrderAndRequestPayment(String idempotencyKey, OrderRequestDto dto, User user) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }

        // 중복 요청 방지를 위한 idempotency key lock
        String lockKey = "IDEMPOTENCY:" + idempotencyKey;
        if (!idempotencyService.tryLock(lockKey)) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
        }

        try {
            // Redis에서 중복 요청 확인
            String cachedUrl = idempotencyService.verifyIdempotencyKey(idempotencyKey, user.getEmail(), IdempotencyContextType.PAYMENT_REQUEST);

            // 중복인 경우 이전 결과 url 반환
            if (cachedUrl != null) {
                return cachedUrl;
            } else {
                // 최초 저장
                idempotencyService.saveInitial(idempotencyKey, user.getEmail(), IdempotencyContextType.PAYMENT_REQUEST);
            }

            // 주문 생성
            Orders orders = setOrder(idempotencyKey, dto, user);

            // 주문 저장
            Orders savedOrder = orderRepository.save(orders);

            // 결제 전략 선택
            PaymentStrategy strategy = strategyResolver.resolve(dto.getPaymentType().name());

            // 결제사별 API 호출 (결제 페이지 요청 후 리다이렉트 URL 받기)
            String redirectUrl = strategy.sendPaymentUrlRequest(orders);

            // Redis에 결과 캐싱 (Used로 변경)
            idempotencyService.markAsUsed(
                    idempotencyKey,
                    user.getEmail(),
                    IdempotencyContextType.PAYMENT_REQUEST,
                    redirectUrl
            );

            log.info("결재 요청 처리 완료 : {}", redirectUrl);

            return redirectUrl;
        } finally {
            // lock 해제
            idempotencyService.unlock(lockKey);
        }

    }

    /**
     * 주문 페이지 데이터
     * @param dto 주문 요청 정보
     * @return 주문 요청 상품 정보 리스트
     */
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

    /**
     * 주문 생성 로직
     * @param dto 주문 요청 정보
     * @param user 주문을 요청한 사용자
     * @param idempotencyKey 중복 요청 방지를 위한 키
     * @return 생성된 주문 객체
     */
    public Orders setOrder(String idempotencyKey, OrderRequestDto dto, User user) {
        // 주문 생성
        Orders orders = Orders.builder()
                .user(user)
                .status(OrderStatus.WAITING_PAYMENT)
                .paymentType(dto.getPaymentType())
                .build();

        // orders의 상품 필드에 상품 추가
        dto.getProductNos().forEach(productId -> {
            // 상품이 판매 중인 경우에만 주문 가능
            Product product = productRepository.findById(productId).orElseThrow(
                    () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getStatus() != ProductStatus.APPROVED) {
                throw new CustomException(ErrorCode.PURCHASE_NOT_AVAILABLE);
            }

            orders.addItem(product);
        });
        return orders;
    }

}
