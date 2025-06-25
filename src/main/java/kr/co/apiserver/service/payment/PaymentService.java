package kr.co.apiserver.service.payment;

import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.PaymentApproveRequestDto;


public interface PaymentService {

   String approvePayment(String idempotencyKey,
                         PaymentApproveRequestDto requestDto,
                         User user
    );

}
