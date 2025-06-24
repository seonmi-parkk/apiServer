package kr.co.apiserver.dto;

import kr.co.apiserver.domain.emums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {
    private List<Long> productNos;
    private PaymentType paymentType;
}
