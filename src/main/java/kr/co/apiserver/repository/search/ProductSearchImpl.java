package kr.co.apiserver.repository.search;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.ProductListResponseDto;
import kr.co.apiserver.util.QueryDslUtil;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.QProduct;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.apiserver.domain.QProductImage.productImage;
import static kr.co.apiserver.domain.QUser.user;

@Log4j2
@Repository
@RequiredArgsConstructor
public class ProductSearchImpl implements ProductSearch {

    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;

    @Override
    public Page<ProductListResponseDto> searchList(PageRequestDto pageRequestDto, Pageable pageable) {
//        Pageable pagable = PageRequest.of(pageRequestDto.getPage() -1,
//                pageRequestDto.getSize(),
//                Sort.by("pno").descending());
        log.info("searchList........");


        List<Long> pnoList = queryFactory
                .select(product.pno)
                .from(product)
                .where(product.status.eq(ProductStatus.APPROVED))
                .orderBy(QueryDslUtil.toOrderSpecifier(pageable.getSort(), product))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Product> result = queryFactory
                .selectFrom(product)
                .leftJoin(product.seller, user).fetchJoin()
                .leftJoin(product.imageList, productImage).fetchJoin()
                .where(product.pno.in(pnoList))
                .orderBy(QueryDslUtil.toOrderSpecifier(pageable.getSort(), product))
                .fetch();


//        List<Product> result = queryFactory
//                .select(product)
//                .from(product)
//                .leftJoin(product.seller, user).fetchJoin()
//                .leftJoin(product.imageList, productImage).fetchJoin()
//                .where(product.status.eq(ProductStatus.APPROVED))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .orderBy(QueryDslUtil.toOrderSpecifier(pageable.getSort(), product))
//                .fetch();

        List<ProductListResponseDto> dtoList = result.stream()
                .map(ProductListResponseDto::fromEntity)
                .toList();

        JPAQuery<Long> totalCount = queryFactory
                .select(product.count())
                .from(product);

        return PageableExecutionUtils.getPage(dtoList, pageable, totalCount::fetchOne);
    }
}
