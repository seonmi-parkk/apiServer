package kr.co.apiserver.util;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

public class QueryDslUtil {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    /**
     * Sort 객체를 OrderSpecifier 배열로 변환하여 Querydsl에서 정렬시 사용할 수 있도록 합니다.
     *
     * @param sort  정렬 정보 (Pageable.getSort())
     * @param qType QueryDSL Q타입 엔티티 (QMember.member)
     * @param <T>   Entity 타입
     * @return QueryDSL 정렬 조건에 사용되는 OrderSpecifier 배열
     */
    public static <T> OrderSpecifier<?>[] toOrderSpecifier(Sort sort, EntityPathBase<T> qType) {
        PathBuilder<T> pathBuilder = new PathBuilder<>(qType.getType(), qType.getMetadata().getName());

        return sort.stream()
                .map(order -> {
                    Expression path = pathBuilder.get(order.getProperty()); // raw type 처리
                    return order.isAscending() ?
                            new OrderSpecifier(Order.ASC, path) :
                            new OrderSpecifier(Order.DESC, path);
                })
                .toArray(OrderSpecifier[]::new);
    }

}
