package kr.co.apiserver.response.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(400, 40000, "BAD_REQUEST"),
    CART_DUPLICATE_ITEM(400, 40001, "이미 장바구니에 담긴 상품입니다."),
    PURCHASE_NOT_AVAILABLE(400, 40011, "해당 상품은 구매가 불가능합니다."),
    INVALID_PAYMENT_REQUEST(400, 40021,"잘못된 결제 요청입니다."),
    UNSUPPORTED_PAYMENT_METHOD(400, 40022, "지원하지 않는 결제 방법입니다."),
    INVALID_IDEMPOTENCY_KEY(400, 40031,"유효하지 않은 Idempotency Key 입니다."),
    UNAUTHORIZED(401, 40100, "UNAUTHORIZED"),
    INVALID_TOKEN(401, 40101, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, 40102, "토큰이 만료되었습니다."),
    NOT_FOUND_TOKEN(401, 40103, "토큰이 존재하지 않습니다."),
    FORBIDDEN(403, 40300, "FORBIDDEN"),
    NOT_FOUND(404, 40400, "NOT_FOUND_DATA"),
    USER_NOT_FOUND(404, 40401, "USER_NOT_FOUND"),
    PRODUCT_NOT_FOUND(404, 40411, "해당 상품이 존재하지 않습니다."),
    ORDER_NOT_FOUND(404, 40421, "해당 주문이 존재하지 않습니다."),
    METHOD_NOT_ALLOWED(405, 40500, "METHOD_NOT_ALLOWED"),
    DUPLICATE_REQUEST(409, 40901, "중복 요청입니다."),
    IDEMPOTENCY_KEY_ALREADY_USED(409, 40911, "이미 사용된 Idempotency Key 입니다."),
    INTERNAL_ERROR(500, 50000, "INTERNAL_SERVER_ERROR");



    private final int status;
    private final int code;
    private final String message;
}
