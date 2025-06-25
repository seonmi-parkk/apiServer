package kr.co.apiserver.service.payment;

import kr.co.apiserver.domain.Orders;
import kr.co.apiserver.dto.PaymentApproveRequestDto;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component("KAKAOPAY")
@Slf4j
public class KakaoPayStrategy implements PaymentStrategy {

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public String sendPaymentUrlRequest(Orders orders){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", orders.getOno().toString());
        params.add("partner_user_id", orders.getUser().getEmail());
        params.add("item_name", orders.getItems().get(0).getProduct().getPname());
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf(orders.getTotalPrice()));
        params.add("tax_free_amount", "0");
        params.add("approval_url", frontendBaseUrl + "/payment/approve?order_id=" + orders.getOno());
        params.add("cancel_url", frontendBaseUrl + "/payment/cancel");
        params.add("fail_url", frontendBaseUrl + "/payment/fail");

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v1/payment/ready",
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String tid = (String) response.getBody().get("tid");
                String redirectUrl = (String) response.getBody().get("next_redirect_pc_url");

                orders.setTid(tid);
                return redirectUrl;

            } else {
                throw new CustomException(ErrorCode.PAYMENT_READY_FAILED);
            }
        } catch (Exception e) {
            log.error("카카오페이 결제 요청 실패", e);
            throw new CustomException(ErrorCode.PAYMENT_API_ERROR);
        }
    }

    @Override
    public void sendPaymentApprovalRequest(Orders order, PaymentApproveRequestDto requestDto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", order.getTid());
        params.add("partner_order_id", order.getOno().toString());
        params.add("partner_user_id", order.getUser().getEmail());
        params.add("pg_token", requestDto.getPgToken());

        try {

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v1/payment/approve",
                    request,
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CustomException(ErrorCode.PAYMENT_APPROVAL_FAILED);
            }
        } catch (Exception e) {
            log.error("카카오페이 결제 승인 실패", e);
            throw new CustomException(ErrorCode.PAYMENT_API_ERROR);
        }
    }

}
