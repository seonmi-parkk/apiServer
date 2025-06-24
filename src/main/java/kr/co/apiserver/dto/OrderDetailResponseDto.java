package kr.co.apiserver.dto;

import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Builder
public class OrderDetailResponseDto {
    private Long ono;
    private LocalDateTime paidAt;
    private Integer totalPrice;
    private OrderItemResponseDto orderItems;
}
