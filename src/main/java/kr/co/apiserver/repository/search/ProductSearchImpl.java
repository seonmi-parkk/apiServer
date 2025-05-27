package kr.co.apiserver.repository.search;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.apiserver.common.util.QueryDslUtil;
import kr.co.apiserver.domain.Product;
import kr.co.apiserver.domain.QProduct;
import kr.co.apiserver.dto.PageRequestDto;
import kr.co.apiserver.dto.PageResponseDto;
import kr.co.apiserver.dto.ProductDto;
import kr.co.apiserver.dto.TodoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static kr.co.apiserver.domain.QProductImage.productImage;
import static kr.co.apiserver.domain.QTodo.todo;

@Log4j2
@Repository
@RequiredArgsConstructor
public class ProductSearchImpl implements ProductSearch {

    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;

    @Override
    public Page<ProductDto> searchList(PageRequestDto pageRequestDto, Pageable pageable) {
//        Pageable pagable = PageRequest.of(pageRequestDto.getPage() -1,
//                pageRequestDto.getSize(),
//                Sort.by("pno").descending());
        log.info("searchList........");

        List<Product> result = queryFactory
                .select(product)
                .from(product)
                .leftJoin(product.imageList, productImage).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(QueryDslUtil.toOrderSpecifier(pageable.getSort(), product))
                .fetch();

        List<ProductDto> dtoList = result.stream()
                .map(ProductDto::fromEntity)
                .toList();

        JPAQuery<Long> totalCount = queryFactory
                .select(product.count())
                .from(product);

        return PageableExecutionUtils.getPage(dtoList, pageable, totalCount::fetchOne);
    }
}
