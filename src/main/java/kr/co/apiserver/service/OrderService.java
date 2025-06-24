package kr.co.apiserver.service;

import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.OrderDetailResponseDto;
import kr.co.apiserver.dto.OrderPreviewResponseDto;
import kr.co.apiserver.dto.OrderRequestDto;

import java.util.List;


public interface OrderService {
    String createOrderAndRequestPayment(String idempotencyKey, OrderRequestDto dto, User user);

    List<OrderPreviewResponseDto> previewOrder(OrderRequestDto dto);

    OrderDetailResponseDto getOrderDetail(Long ono, User user);
}
