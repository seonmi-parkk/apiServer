package kr.co.apiserver.controller;

import kr.co.apiserver.dto.CartItemDto;
import kr.co.apiserver.dto.CartItemListDto;
import kr.co.apiserver.response.ApiResponse;
import kr.co.apiserver.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;

    // 로그인한 사용자만 자신의 장바구니에 아이템 추가 가능
    @PreAuthorize("#itemDto.email == authentication.name")
    @PostMapping
    public ApiResponse<List<CartItemListDto>> addItem(@RequestBody CartItemDto itemDto) {
        return ApiResponse.ok(cartService.addItem(itemDto));
    }

    // 장바구니 아이템 조회
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ApiResponse<List<CartItemListDto>> getCartItemList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ApiResponse.ok(cartService.findCartItems(email));
    }

    // 장바구니 아이템 삭제
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{cino}")
    public ApiResponse<List<CartItemListDto>> removeItem(@PathVariable Long cino) {
        return ApiResponse.ok(cartService.removeItem(cino));
    }



}
