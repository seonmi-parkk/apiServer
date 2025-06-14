package kr.co.apiserver.service;

import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.OrderStatus;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.CartItemListDto;
import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.repository.OrderRepository;
import kr.co.apiserver.repository.ProductRepository;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;

    @Override
    public String createOrderAndRequestPayment(OrderRequestDto dto, User user){
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

        // 결재 요청
        return paymentService.requestPayment(orders);
    }

    @Override
    public List<CartItemListDto> previewOrder(OrderRequestDto dto) {
        // 상품 정보 조회
        productRepository.getOrderPrivewInfo(dto.getProductNos())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return null;
    }
}
