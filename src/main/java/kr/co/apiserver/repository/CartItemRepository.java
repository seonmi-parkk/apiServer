package kr.co.apiserver.repository;

import kr.co.apiserver.domain.CartItem;
import kr.co.apiserver.dto.CartItemListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 유저 email로 모든 장바구니 아이템 가져오기
    @Query("select " +
            "new kr.co.apiserver.dto.CartItemListDto(ci.cino, p.pno, p.pname, p.price, p.status, pi.fileName) " +
            "from CartItem ci inner join Cart c on ci.cart = c " +
            " join Product p on ci.product = p " +
            " join p.imageList pi " +
            "where c.user.email = :email and pi.ord = 0 " +
            "order by ci.cino desc")
    List<CartItemListDto> findCartItemsByEmail(String email);

    // 해당 상품이 장바구니 아이템으로 존재하는지 확인
    @Query("select ci from Cart c join CartItem ci on ci.cart = c " +
            "where c.user.email = :email and ci.product.pno = :pno")
    Optional<CartItem> findCartItemByEmailAndPno(String email, Long pno);

    // 장바구니 아이템 번호로 장바구니 번호 확인
    @Query("select c.cno from Cart c join CartItem ci on c = ci.cart where ci.cino = :cino")
    Long findCnoByCino(Long cino);

    // 장바구니 no로 모든 장바구니 아이템 조회
    @Query("select " +
            "new kr.co.apiserver.dto.CartItemListDto(ci.cino, p.pno, p.pname, p.price, p.status, pi.fileName) " +
            "from CartItem ci join Cart c on ci.cart = c " +
            "join Product p on ci.product = p " +
            "left join p.imageList pi " +
            "where c.cno = :cno and pi.ord = 0 " +
            "order by ci.cino desc")
    List<CartItemListDto> findCartItemsByCno(Long cno);

}
