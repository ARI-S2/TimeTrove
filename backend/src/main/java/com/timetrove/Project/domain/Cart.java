package com.timetrove.Project.domain;


import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId; // PK

    @ManyToOne
    @JoinColumn(name = "user_code", referencedColumnName = "user_code", nullable = false)
    private User user; // 회원

    @ManyToOne
    @JoinColumn(name = "Product_Mgt_id", nullable = false)
    private ProductManagement productManagement; // 상품

    @Setter
    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량

    @Setter
    @Column(name = "price", nullable = false)
    private Long price; // 가격

    public Cart(User user, ProductManagement productManagement, Long quantity, Long price) {
        this.user = user;
        this.productManagement = productManagement;
        this.quantity = quantity;
        this.price = price;
    }

    public Cart() {

    }

}