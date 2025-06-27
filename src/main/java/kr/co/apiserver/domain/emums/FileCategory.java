package kr.co.apiserver.domain.emums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileCategory {

    PRODUCT("product"),
    PROFILE("profile");

    private final String value;
}
