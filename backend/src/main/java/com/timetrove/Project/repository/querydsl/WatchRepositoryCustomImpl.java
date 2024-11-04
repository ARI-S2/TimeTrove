package com.timetrove.Project.repository.querydsl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.timetrove.Project.domain.QWatch;
import com.timetrove.Project.domain.Watch;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class WatchRepositoryCustomImpl implements WatchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public WatchRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Watch> findWatchesWithFilter(String searchWord, String filter, Pageable pageable) {
        QWatch watch = QWatch.watch;

        JPAQuery<Watch> query = queryFactory
                .selectFrom(watch)
                .where(
                        searchWord != null && !searchWord.isEmpty() ?
                                watch.name.containsIgnoreCase(searchWord)
                                        .or(watch.model.containsIgnoreCase(searchWord))
                                : null
                );

        // 정렬 조건 적용
        switch (filter) {
            case "hit":
                query.orderBy(watch.viewCount.desc());
                break;
            case "priceHigh":
                query.orderBy(watch.soldPrice.desc());
                break;
            case "priceLow":
                query.orderBy(watch.soldPrice.asc());
                break;
            default:
                query.orderBy(watch.id.desc());
        }

        // 페이지네이션 적용
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 전체 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(watch.count())
                .from(watch)
                .where(
                        searchWord != null && !searchWord.isEmpty() ?
                                watch.name.containsIgnoreCase(searchWord)
                                        .or(watch.model.containsIgnoreCase(searchWord))
                                : null
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }
}
