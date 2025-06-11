package kr.co.apiserver.service;

import kr.co.apiserver.domain.Order;

public interface PaymentService {
    String requestPayment(Order order);
}
