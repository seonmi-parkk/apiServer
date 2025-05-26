package kr.co.apiserver.repository;

import kr.co.apiserver.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepositroy extends JpaRepository<Product, Long> {
}
