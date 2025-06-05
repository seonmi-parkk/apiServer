package kr.co.apiserver.service;

import kr.co.apiserver.domain.Cart;
import kr.co.apiserver.domain.CartItem;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.CartItemDto;
import kr.co.apiserver.dto.CartItemListDto;
import kr.co.apiserver.repository.CartItemRepository;
import kr.co.apiserver.repository.CartRepository;
import kr.co.apiserver.repository.ProductRepository;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CartItemListDto> addItem(CartItemDto cartItemDto) {

        String email = cartItemDto.getEmail();
        Long pno = cartItemDto.getPno();

        CartItem cartItem = null;

        // 이미 장바구니에 해당 아이템이 있는 경우 예외 처리
        Optional<CartItem> optionalCartItem = cartItemRepository.findCartItemByEmailAndPno(email, pno);
        optionalCartItem.ifPresent(throwable -> {
            throw new CustomException(ErrorCode.CART_DUPLICATE_ITEM);
        });

        Cart cart = findCart(email);

        // 장바구니 아이템 생성
        Product product = productRepository.findById(pno).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .build();

        // 장바구니 아이템 저장
        cartItemRepository.save(cartItem);

        // 장바구니 아이템 목록 반환
        return findCartItems(email);
    }

    private Cart findCart(String email) {

        Cart cart = null;
        // 해당 email의 장바구니 조회
        Optional<Cart> OptionalCart = cartRepository.findByUser_Email(email);

        // 없으면 생성
        if (OptionalCart.isEmpty()) {
            User user = User.builder().email(email).build();
            cart = Cart.builder().user(user).build();
            cart = cartRepository.save(cart);
        } else {
            cart = OptionalCart.get();
        }

        return cart;
    }

    @Override
    public List<CartItemListDto> findCartItems(String email) {
        return cartItemRepository.findCartItemsByEmail(email);
    }

    @Override
    public List<CartItemListDto> removeItem(Long cino) {
        Long cno = cartItemRepository.findCnoByCino(cino);
        cartItemRepository.deleteById(cino);

        return cartItemRepository.findCartItemsByCno(cno);
    }
}
