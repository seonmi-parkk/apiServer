package kr.co.apiserver.response.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(400, 40000, "BAD_REQUEST"),
    CART_DUPLICATE_ITEM(400, 40001, "이미 장바구니에 담긴 상품입니다."),
    PURCHASE_NOT_AVAILABLE(400, 40002, "해당 상품은 구매가 불가능합니다."),
    UNAUTHORIZED(401, 40100, "UNAUTHORIZED"),
    INVALID_TOKEN(401, 40101, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, 40102, "토큰이 만료되었습니다."),
    NOT_FOUND_TOKEN(401, 40103, "토큰이 존재하지 않습니다."),
    FORBIDDEN(403, 40300, "FORBIDDEN"),
    NOT_FOUND(404, 40400, "NOT_FOUND_DATA"),
    USER_NOT_FOUND(404, 40401, "USER_NOT_FOUND"),
    PRODUCT_NOT_FOUND(404, 40402, "해당 상품이 존재하지 않습니다."),
    METHOD_NOT_ALLOWED(405, 40500, "METHOD_NOT_ALLOWED"),
    INTERNAL_ERROR(500, 50000, "INTERNAL_SERVER_ERROR");


    private final int status;
    private final int code;
    private final String message;
}
