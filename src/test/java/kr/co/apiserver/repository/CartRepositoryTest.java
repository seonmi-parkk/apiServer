package kr.co.apiserver.repository;

import kr.co.apiserver.domain.Cart;
import kr.co.apiserver.domain.CartItem;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.User;
import kr.co.apiserver.dto.CartItemListDto;
import kr.co.apiserver.response.exception.CustomException;
import kr.co.apiserver.response.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Slf4j
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void testAddProductIntoCart(){
        String email = "user11@test.com";
        Long pno = 104L;
        
        // 장바구니 아이템 확인 있으면 예외 메세지
        Optional<CartItem> cartItem = cartItemRepository.findCartItemByEmailAndPno(email, pno);
        if(!cartItem.isEmpty()){
           throw new CustomException(ErrorCode.DUPLICATED_CART_ITEM);
        }

        Optional<Cart> OptionalCart = cartRepository.findByUser_Email(email);

        Cart cart = null;
        // 장바구니 자체가 없는 경우 생성
        if(OptionalCart.isEmpty()){
            User user = User.builder().email(email).build();
            Cart tempCart = Cart.builder().user(user).build();

            cart = cartRepository.save(tempCart);

        }else { // 장바구니는 있으나 아이템이 없는 경우
            cart = OptionalCart.get();
        }

        // 장바구니 아이템 생성 및 저장
        Product product = Product.builder().pno(pno).build();
        CartItem newCartItem = CartItem.builder().cart(cart).product(product).build();
        cartItemRepository.save(newCartItem);


    }

    @Test
    public void testListOfMember(){
        String email = "user11@test.com";

        List<CartItemListDto> cartItemList = cartItemRepository.findCartItemsByEmail(email);
        log.info("email");
        cartItemList.forEach(cartItem -> {
            log.info(cartItem.toString());
        });

    }

    @Test
    public void testDeleteThenGetList() {
        Long cino = 9L;

        Long cno = cartItemRepository.findCnoByCino(cino);
        cartItemRepository.deleteById(cino);

        List<CartItemListDto> cartItems = cartItemRepository.findCartItemsByCno(cno);

        cartItems.forEach(cartItem -> {
            log.info(cartItem.toString());
        });

    }
}