package kr.co.apiserver.domain;

public enum ProductStatus {
    PENDING, // 승인 대기
    APPROVED, // 승인 완료 (판매중)
    REJECTED, // 승인 거절
    PAUSED, // 판매 중지
    DELETED // 삭제
}
