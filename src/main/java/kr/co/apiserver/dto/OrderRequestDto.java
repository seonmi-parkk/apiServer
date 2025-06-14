package kr.co.apiserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {
    private List<Long> productNos;
    private String paymentMethod;
}
