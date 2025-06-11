package kr.co.apiserver.domain;

import jakarta.persistence.*;
import kr.co.apiserver.domain.emums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

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

    private String tid;

    @Column(nullable = false)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
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
