package kr.co.apiserver.service;

import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoPayService implements PaymentService {

    private final OrderRepository orderRepository;

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;
    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public String requestPayment(Orders orders){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", orders.getOno().toString());
        params.add("partner_user_id", orders.getUser().getEmail());
        params.add("item_name", "디지털 아트 구매");
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf(orders.getTotalPrice()));
        params.add("tax_free_amount", "0");
        params.add("approval_url", frontendBaseUrl + "/payment/success?orderId=" + orders.getOno());
        params.add("cancel_url", frontendBaseUrl + "/payment/cancel");
        params.add("fail_url", frontendBaseUrl + "/payment/fail");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v1/payment/ready",
                request,
                Map.class
        );

        String tid = (String) response.getBody().get("tid");
        String redirectUrl = (String) response.getBody().get("next_redirect_pc_url");

        orders.setTid(tid);
        orderRepository.save(orders);

        return redirectUrl;
    }
}
