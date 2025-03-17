package com.timetrove.Project.repository.querydsl;

import com.timetrove.Project.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> findProductsWithFilter(String searchWord, String filter, Pageable pageable);
}
