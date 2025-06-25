package kr.co.apiserver.domain.emums;

public enum PaymentIdempotencyStatus {
    READY, // 결제 요청 전
    USED // 결제 요청 후
}
