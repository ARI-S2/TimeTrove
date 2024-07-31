package com.timetrove.Project.repository;

import java.util.List;

import com.timetrove.Project.domain.Watch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class WatchRepositoryCustomImpl implements WatchRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Watch> watchFindData(String searchWord, int start, String filter) {
        String baseQuery = "SELECT * FROM watch "
        				+ "WHERE name LIKE CONCAT('%',:searchWord,'%') "
        				+ "OR model LIKE CONCAT('%',:searchWord,'%')";

        String orderClause = "";
        switch (filter) {
            case "hit":
                orderClause = " ORDER BY hit DESC";
                break;
            case "priceHigh":
                orderClause = " ORDER BY s_price DESC";
                break;
            case "priceLow":
                orderClause = " ORDER BY s_price ASC";
                break;
            default:
                orderClause = " ORDER BY no DESC";
                break;
        }

        String finalQuery = baseQuery + orderClause + " LIMIT :start, 6";

        Query query = entityManager.createNativeQuery(finalQuery, Watch.class);
        query.setParameter("searchWord", searchWord);
        query.setParameter("start", start);

        return query.getResultList();
    }
}
