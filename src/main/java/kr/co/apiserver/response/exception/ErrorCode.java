package kr.co.apiserver.response.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(400, 40000, "BAD_REQUEST"),
    UNAUTHORIZED(401, 40100, "UNAUTHORIZED"),
    INVALID_REFRESH_TOKEN(401, 40101, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(401, 40102, "리프레시 토큰이 만료되었습니다."),
    NOT_FOUND_REFRESH_TOKEN(401, 40103, "리프레시 토큰이 존재하지 않습니다."),
    FORBIDDEN(403, 40300, "FORBIDDEN"),
    NOT_FOUND(404, 40400, "NOT_FOUND_DATA"),
    INTERNAL_ERROR(500, 50000, "INTERNAL_SERVER_ERROR");

    private final int status;
    private final int code;
    private final String message;
}
