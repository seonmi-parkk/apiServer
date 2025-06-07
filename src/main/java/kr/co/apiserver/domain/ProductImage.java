package kr.co.apiserver.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pino;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private int ord; // 이미지 순서 또는 대표 여부 구분

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pno", nullable = false)
    private Product product;

}
