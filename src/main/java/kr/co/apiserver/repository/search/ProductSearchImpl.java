package kr.co.apiserver.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.apiserver.domain.emums.ProductStatus;
import kr.co.apiserver.domain.emums.SearchType;
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

        // 키워드 검색
        if (pageRequestDto.getKeyword() != null && !pageRequestDto.getKeyword().isBlank()) {
            String keyword = pageRequestDto.getKeyword();
            SearchType type = pageRequestDto.getType();

            if (type != null) {
                switch (type) {
                    case TITLE -> builder.and(product.pname.containsIgnoreCase(keyword));
                    case CONTENT -> builder.and(product.pdesc.containsIgnoreCase(keyword));
                    case TITLE_CONTENT -> builder.and(
                            product.pname.containsIgnoreCase(keyword)
                                    .or(product.pdesc.containsIgnoreCase(keyword))
                    );
                }
            }
        }

        // 가격 조건
        if (pageRequestDto.getMinPrice() != null) {
            builder.and(product.price.goe(pageRequestDto.getMinPrice()));
        }
        if (pageRequestDto.getMaxPrice() != null) {
            builder.and(product.price.loe(pageRequestDto.getMaxPrice()));
        }

        // 카테고리 필터링
        if (pageRequestDto.getCategories() != null && !pageRequestDto.getCategories().isEmpty()) {
            builder.and(product.productCategories.any().category.cgno.in(pageRequestDto.getCategories()));
        }

        // 정렬 조건
        OrderSpecifier<?> orderSpecifier;
        switch (pageRequestDto.getSortBy()) {
            case SALES -> orderSpecifier = product.salesCount.desc();
            case PRICE_ASC -> orderSpecifier = product.price.asc();
            case PRICE_DESC -> orderSpecifier = product.price.desc();
            case LATEST -> orderSpecifier = product.pno.desc();
            default -> orderSpecifier = product.pno.desc();
        }

        // 상품 ID List 조회
        List<Long> pnoList = queryFactory
                .select(product.pno)
                .from(product)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조회된 상품 ID가 없으면 빈 페이지 반환
        if (pnoList.isEmpty()) {
            return PageableExecutionUtils.getPage(List.of(), pageable, () -> 0L);
        }

        // 상품 ID로 상품 정보 조회, 이미지 fetch Join
        List<Product> result = queryFactory
                .selectFrom(product)
                .leftJoin(product.seller, user).fetchJoin()
                .leftJoin(product.imageList, productImage).fetchJoin()
                .where(product.pno.in(pnoList))
                .orderBy(orderSpecifier)
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
