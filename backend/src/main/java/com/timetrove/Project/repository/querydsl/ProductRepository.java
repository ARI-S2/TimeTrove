package com.timetrove.Project.repository.querydsl;

import com.timetrove.Project.domain.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {


}