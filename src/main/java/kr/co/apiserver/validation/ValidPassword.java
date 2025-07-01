package kr.co.apiserver.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "비밀번호는 영문, 숫자, 특수문자 중 2가지 이상 조합되어야 하며, 동일문자 3회 이상 반복 불가합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
