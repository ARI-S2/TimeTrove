package com.timetrove.Project.common.config;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class QuerydslConfig {

    // EntityManager에 의존성 주입을  담당
    @PersistenceContext
    // JPA에서 엔터티의 CRUD 수행하는 객체
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        //쿼리를 작성하는 JPAQueryFactory에 EntityManager를 넘겨 사용
        return new JPAQueryFactory(entityManager);
    }
}
