package kr.co.apiserver.domain.emums;

import lombok.Getter;

@Getter
public enum PaymentType {

    KAKAOPAY("카카오 페이");

    private final String paymentName;

    PaymentType(String paymentName) {
        this.paymentName = paymentName;
    }

}
