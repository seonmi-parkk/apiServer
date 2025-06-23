package kr.co.apiserver.service;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.OrderStatus;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.OrderPreviewResponseDto;
import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.repository.OrderRepository;
import kr.co.apiserver.repository.ProductRepository;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
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
    private final PaymentService paymentService;
    private final RedisTemplate<String, String> redisTemplate;


    @Transactional
    @Override
    public String createOrderAndRequestPayment(String idempotencyKey, OrderRequestDto dto, User user){

        // 중복 요청 여부 확인
        String redisKey = "payment:request:" + idempotencyKey;
        log.info("dto.getIdempotencyKey(): {}", idempotencyKey);
        // Redis에서 먼저 중복 요청 여부 확인
        String cachedRedirectUrl = redisTemplate.opsForValue().get(redisKey);
        if (cachedRedirectUrl != null) {
            return cachedRedirectUrl;  // 중복요청의 경우 기존 결과 반환
        }

        // db에서 중복 확인
        Optional<Orders> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return existing.get().getRedirectUrl();  // 중복 요청이면 기존 URL 반환
        }

        // 주문 생성
        Orders orders = Orders.builder()
                .user(user)
                .status(OrderStatus.WAITING_PAYMENT)
                .build();

        // 주문 상품 추가
        dto.getProductNos().forEach(productId -> {
            // 상품이 판매 중인 경우에만 주문 가능
            Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            if (product.getStatus() != ProductStatus.APPROVED) {
                throw new CustomException(ErrorCode.PURCHASE_NOT_AVAILABLE);
            }
            orders.addItem(product);
        });
        // 주문 저장
        orderRepository.save(orders);

        // 결제 페이지 요청 후 리다이렉트 URL 받기
        String redirectUrl = paymentService.requestPaymentUrl(orders);

        // Redis에 결과 캐싱
        redisTemplate.opsForValue().set(redisKey, redirectUrl, Duration.ofMinutes(30));

        return redirectUrl;
    }

    @Override
    public List<OrderPreviewResponseDto> previewOrder(OrderRequestDto dto) {
        // 상품 정보 조회
        return productRepository.getOrderPrivewInfo(dto.getProductNos())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
