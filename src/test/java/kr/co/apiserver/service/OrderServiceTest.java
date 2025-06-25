package kr.co.apiserver.service;

import kr.co.apiserver.domain.User;
import kr.co.apiserver.domain.emums.PaymentType;
import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    private static final int THREAD_COUNT = 10;

    @Test
    void testIdempotencyKeyConcurrency() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        String idempotencyKey = UUID.randomUUID().toString();
        String userId = "user11@test.com";
        User user = userRepository.findById(userId).orElseThrow();

        OrderRequestDto dto = new OrderRequestDto();
        dto.setPaymentType(PaymentType.KAKAOPAY);
        dto.setProductNos(List.of(303L, 305L));

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    String redirectUrl = orderService.createOrderAndRequestPayment(idempotencyKey, dto, user);
                    System.out.println("Thread result: " + redirectUrl);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }

}
