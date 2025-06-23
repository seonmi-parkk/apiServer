package kr.co.apiserver.service;

import kr.co.apiserver.domain.Orders;

public interface PaymentService {
    String requestPaymentUrl(Orders orders);
}
