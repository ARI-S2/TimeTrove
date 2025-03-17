package com.timetrove.Project.repository.querydsl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.timetrove.Project.domain.Product;
import com.timetrove.Project.domain.QProduct;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Product> findProductsWithFilter(String searchWord, String filter, Pageable pageable) {
        QProduct product = QProduct.product;

        JPAQuery<Product> query = queryFactory
                .selectFrom(product)
                .where(
                        searchWord != null && !searchWord.isEmpty() ?
                                product.productName.containsIgnoreCase(searchWord)
                                        .or(product.model.containsIgnoreCase(searchWord))
                                : null
                );

        // 정렬 조건 적용
        switch (filter) {
//            case "hit":
//                query.orderBy(product.viewCount.desc());
//                break;
            case "priceHigh":
                query.orderBy(product.price.desc());
                break;
            case "priceLow":
                query.orderBy(product.price.asc());
                break;
            default:
                query.orderBy(product.productId.desc());
        }

        // 페이지네이션 적용
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 전체 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        searchWord != null && !searchWord.isEmpty() ?
                                product.productName.containsIgnoreCase(searchWord)
                                        .or(product.model.containsIgnoreCase(searchWord))
                                : null
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }
}
