package com.timetrove.Project.domain;

import com.siot.IamportRestClient.response.payco.OrderStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "order_item")
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;  // PK

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;  // 주문 (N:1)

    @ManyToOne
    @JoinColumn(name = "product_management_id", nullable = false)
    private ProductManagement productManagement;  // 상품 (N:1)

    @Column(name = "quantity", nullable = false)
    private Long quantity;  // 주문된 상품 개수

    @Column(name = "price", nullable = false)
    private BigDecimal price;  // 주문 시점의 상품 가격

    @Builder
    public OrderItem(Order order, ProductManagement productManagement, Long quantity, BigDecimal price) {
        this.order = order;
        this.productManagement = productManagement;
        this.quantity = quantity;
        this.price = price;
    }

}
