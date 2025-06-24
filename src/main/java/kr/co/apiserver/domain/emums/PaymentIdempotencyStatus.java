package kr.co.apiserver.domain.emums;

public enum PaymentIdempotencyStatus {
    READY, // 결제 요청 전
    USED, // 결제 요청 후
    PAID // 결제 승인 완료 후
}
