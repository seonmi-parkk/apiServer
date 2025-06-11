package kr.co.apiserver.controller;

import kr.co.apiserver.dto.OrderRequestDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.security.UserDetailsImpl;
import kr.co.apiserver.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 결재 요청
    @PostMapping
    public ApiResponse<Map<String, String>> requestPayment(@RequestBody OrderRequestDto dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String paymentUrl = orderService.createOrderAndRequestPayment(dto, userDetails.getUser());
        return ApiResponse.ok(Map.of("paymentUrl",paymentUrl));
    }
}
