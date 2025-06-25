package kr.co.apiserver.dto;

import kr.co.apiserver.domain.emums.PaymentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailResponseDto {
    private Long ono;
    private LocalDateTime paidAt;
    private String paymentType;
    private int totalPrice;
    private List<OrderItemResponseDto> OrderItems;
    private String email;


    public OrderDetailResponseDto(Long ono, LocalDateTime paidAt, PaymentType paymentType, int totalPrice, String email) {
        this.ono = ono;
        this.paidAt = paidAt;
        this.paymentType = paymentType.getPaymentName();
        this.totalPrice = totalPrice;
        this.email = email;
    }
}
