package kr.co.apiserver.service.payment;

import jakarta.annotation.PostConstruct;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStrategyResolver {

    private final Map<String, PaymentStrategy> strategyMap;

    @PostConstruct
    public void init() {
        log.info("strategyMap keys: {}", strategyMap.keySet());
    }

    public PaymentStrategy resolve(String paymentMethod) {
        PaymentStrategy strategy = strategyMap.get(paymentMethod);
        if (strategy == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_PAYMENT_METHOD);
        }
        return strategy;
    }
}
