package kr.co.apiserver.service;

import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.OrderRequestDto;


public interface OrderService {
    String createOrderAndRequestPayment(OrderRequestDto dto, User user);
}
