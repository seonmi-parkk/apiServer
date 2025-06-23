package kr.co.apiserver.domain;

import jakarta.persistence.*;
import kr.co.apiserver.domain.emums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", nullable = false)
    private User user;

    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime paidAt;

    private String tid; // 카카오 결제 TID
    private String redirectUrl; // 결제 페이지 URL

    @Column(unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(Product product) {
        OrderItem item = OrderItem.builder()
                .order(this)
                .product(product)
                .price(product.getPrice())
                .build();
        this.items.add(item);
        this.totalPrice += product.getPrice();
    }

}
