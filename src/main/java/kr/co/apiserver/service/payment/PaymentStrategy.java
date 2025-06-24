package kr.co.apiserver.service.payment;


import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.PaymentApproveRequestDto;

public interface PaymentStrategy {
    String requestPaymentUrl(Orders order);
    String approvePayment(String idempotencyKey, PaymentApproveRequestDto dto, User user);
}
