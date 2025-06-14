package kr.co.apiserver.domain.emums;

import lombok.Getter;

@Getter
public enum ProductStatus {
    PENDING("승인 대기"),
    APPROVED("판매 중"), // 승인완료
    REJECTED("승인 거절"),
    PAUSED("판매 중지"),
    DELETED("삭제");

    private final String message;

    ProductStatus(String message) {
        this.message = message;
    }
}
