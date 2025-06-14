package kr.co.apiserver.service;

import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.CartItemListDto;
import kr.co.apiserver.dto.OrderRequestDto;

import java.util.List;


public interface OrderService {
    String createOrderAndRequestPayment(OrderRequestDto dto, User user);

    List<CartItemListDto> previewOrder(OrderRequestDto dto);
}
