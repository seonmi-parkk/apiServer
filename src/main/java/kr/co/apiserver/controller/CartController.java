package kr.co.apiserver.controller;

import kr.co.apiserver.dto.CartItemDto;
import kr.co.apiserver.dto.CartItemListDto;
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
    public List<CartItemListDto> addItem(@RequestBody CartItemDto itemDto) {
        log.info(itemDto);
        return cartService.addItem(itemDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public List<CartItemListDto> getCartItemList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("email: " + email);

        return cartService.findCartItems(email);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{cino}")
    public List<CartItemListDto> removeItem(@PathVariable Long cino) {
        log.info("cino: " + cino);
        return cartService.removeItem(cino);
    }



}
