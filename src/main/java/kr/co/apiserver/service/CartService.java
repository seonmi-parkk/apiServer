package kr.co.apiserver.service;

import kr.co.apiserver.dto.CartItemDto;
import kr.co.apiserver.dto.CartItemListDto;

import java.util.List;

public interface CartService {

    List<CartItemListDto> addItem(CartItemDto cartItemDto);

    List<CartItemListDto> findCartItems(String email);

    List<CartItemListDto> removeItem(Long cino);
}
