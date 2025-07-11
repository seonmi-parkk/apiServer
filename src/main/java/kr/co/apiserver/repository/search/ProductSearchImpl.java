package kr.co.apiserver.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.dto.ProductListResponseDto;
import kr.co.apiserver.util.QueryDslUtil;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.QProduct;
import kr.co.apiserver.dto.PageRequestDto;
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
        // 검색 조건
        BooleanBuilder builder = new BooleanBuilder();
        // 판매중인 상품만
        builder.and(product.status.eq(ProductStatus.APPROVED));

        // 카테고리 필터링
        if (pageRequestDto.getCategories() != null && !pageRequestDto.getCategories().isEmpty()) {
            builder.and(product.productCategories.any().category.cgno.in(pageRequestDto.getCategories()));
        }

        // 상품 ID List 조회
        List<Long> pnoList = queryFactory
                .select(product.pno)
                .from(product)
                .where(builder)
                .orderBy(QueryDslUtil.toOrderSpecifier(pageable.getSort(), product))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 상품 ID로 상품 정보 조회, 이미지 fetch Join
        List<Product> result = queryFactory
                .selectFrom(product)
                .leftJoin(product.seller, user).fetchJoin()
                .leftJoin(product.imageList, productImage).fetchJoin()
                .where(product.pno.in(pnoList))
                .orderBy(QueryDslUtil.toOrderSpecifier(pageable.getSort(), product))
                .fetch();

        // DTO 변환
        List<ProductListResponseDto> dtoList = result.stream()
                .map(ProductListResponseDto::fromEntity)
                .toList();

        // totalCount
        JPAQuery<Long> totalCount = queryFactory
                .select(product.count())
                .from(product)
                .where(builder);

        return PageableExecutionUtils.getPage(dtoList, pageable, totalCount::fetchOne);
    }
}
