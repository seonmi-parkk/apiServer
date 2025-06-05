package kr.co.apiserver.repository;

import jakarta.transaction.Transactional;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.dto.PageRequestDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testInsert() {

        for (int i = 0; i < 10; i++) {
            Product product = Product.builder().pname("test").pdesc("desc").price(10000).build();

            product.addImageString(UUID.randomUUID()+"_"+"image1.jpg");
            product.addImageString(UUID.randomUUID()+"_"+"image2.jpg");

            productRepository.save(product);
        }

    }

    @Test
    @Transactional
    public void testRead() {
        Long pno = 2L;

        Product product = productRepository.findById(pno).orElseThrow();
        log.info(product);
        log.info(product.getImageList());
    }

    @Test
    public void testRead2() {
        Long pno = 2L;

        Product product = productRepository.selectOne(pno).orElseThrow();
        log.info(product);
        log.info(product.getImageList());
    }

    @Commit
    @Transactional
    @Test
    public void testDelete() {
        Long pno = 2L;

        productRepository.updateToDelete(2L, true);
        log.info("Deleted product with pno: " + pno);
    }

    @Test
    public void testUpdate() {
        Product product = productRepository.selectOne(3L).orElseThrow(NoSuchElementException::new);
        product.changePrice(3000);

        //product.clearList();
        product.addImageString(UUID.randomUUID()+"_"+"Pimage1.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"Pimage2.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"Pimage3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testSearch() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();
        productRepository.searchList(pageRequestDto, pageRequestDto.toPageable());
    }

}
