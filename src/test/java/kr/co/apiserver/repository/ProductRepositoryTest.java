package kr.co.apiserver.repository;

import kr.co.apiserver.domain.Product;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductRepositoryTest {

    @Autowired
    private ProductRepositroy productRepository;

    @Test
    public void testInsert() {
        Product product = Product.builder().name("test").pdesc("desc").price(10000).build();

        product.addImageString(UUID.randomUUID()+"_"+"image1.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"image2.jpg");

        productRepository.save(product);

    }

}
