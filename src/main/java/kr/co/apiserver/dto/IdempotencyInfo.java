package kr.co.apiserver.dto;

import kr.co.apiserver.domain.emums.PaymentIdempotencyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyInfo {
    private PaymentIdempotencyStatus status;
    private String resultUrl;
}
