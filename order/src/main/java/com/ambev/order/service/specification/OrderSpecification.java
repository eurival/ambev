
package com.ambev.order.service.specification;

import com.ambev.order.domain.Order;
import com.ambev.order.domain.Order_;
import com.ambev.order.service.criteria.OrderCriteria;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;


public class OrderSpecification {

    public static Specification<Order> createSpecification(OrderCriteria criteria) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (criteria.getOrderId() != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Order_.orderId), criteria.getOrderId()));
            }

            if (criteria.getStatus() != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Order_.status), criteria.getStatus()));
            }

            if (criteria.getIntegrado() != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Order_.integrado), criteria.getIntegrado()));
            }

            if (criteria.getOrderDateFrom() != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(Order_.orderDate), criteria.getOrderDateFrom()));
            }

            if (criteria.getOrderDateTo() != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(Order_.orderDate), criteria.getOrderDateTo()));
            }

            if (criteria.getTotalValueMin() != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(Order_.totalValue), criteria.getTotalValueMin()));
            }

            if (criteria.getTotalValueMax() != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(Order_.totalValue), criteria.getTotalValueMax()));
            }

            return predicate;
        };
    }
}
