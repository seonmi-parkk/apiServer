package kr.co.apiserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentApproveRequestDto {
    private Long orderId;
    private String pgToken;
}
