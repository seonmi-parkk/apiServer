package kr.co.apiserver.service;

import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@Slf4j
public class ProductSerivceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testList(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();
        PageResponseDto<ProductDto> responseDto = productService.getList(pageRequestDto);
        log.info("이미지: "+responseDto.getDtoList().get(6).getUploadedFileNames().toString());
    }
    @Test
    public void testRegister() {
        ProductDto productDto = ProductDto.builder()
                .name("새로운 상품")
                .desc("상품 설명")
                .price(1000)
                .build();

        productDto.setUploadedFileNames(
                java.util.List.of(
                        UUID.randomUUID() + "_"+"test1.jpg",
                        UUID.randomUUID() + "_"+"test2.jpg"
                )
        );

        productService.register(productDto);
    }

}
