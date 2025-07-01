package kr.co.apiserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        // 영문, 숫자, 특수문자 증 2가지 이상 조합 여부 검사
        boolean hasLetter = value.matches(".*[a-zA-Z].*");
        boolean hasNumber = value.matches(".*\\d.*");
        boolean hasSpecial = value.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int count = 0;
        if (hasLetter) count++;
        if (hasNumber) count++;
        if (hasSpecial) count++;

        boolean hasTwoTypes = count >= 2;

        // 동일문자 3회 이상 반복 여부 검사
        boolean hasNoRepeat = !value.matches(".*(.)\\1\\1.*");

        return hasTwoTypes && hasNoRepeat;
    }
}
