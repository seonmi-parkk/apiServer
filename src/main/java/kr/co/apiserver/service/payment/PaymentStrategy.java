package kr.co.apiserver.service.payment;


import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.dto.PaymentApproveRequestDto;

public interface PaymentStrategy {

    // 결제 승인 요청을 결제사별로 위임
    void sendPaymentApprovalRequest(Orders order, PaymentApproveRequestDto requestDto);

    // 결제 URL 요청을 결제사별로 위임 (결제 페이지 URL 리턴)
    String sendPaymentUrlRequest(Orders order);

}
