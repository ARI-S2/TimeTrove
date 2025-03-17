package com.timetrove.Project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "product_management")
@NoArgsConstructor
public class ProductManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_stock")
    private Long quantity; // 현재 재고 수량

    @Column(name = "is_soldOut")
    private boolean isSoldOut; // 품절 여부

    @Column(name = "is_restocked")
    private boolean isRestocked; // 최근 재입고 여부

    @Column(name = "is_restockAvailable")
    private boolean isRestockAvailable; // 재입고 가능 여부

    @Column(name = "view_count")
    private int viewCount = 0; // 조회수

    public void updateInventory(Long quantity, Boolean isRestockAvailable, Boolean isRestocked, Boolean isSoldOut, int viewCount) {
        this.quantity = quantity;
        this.isRestockAvailable = isRestockAvailable;
        this.isRestocked = isRestocked;
        this.isSoldOut = isSoldOut;
        this.viewCount = viewCount;
    }

    public void decreaseQuantity(Long quantity) {
        if (this.isSoldOut) {
            throw new RuntimeException("이미 품절된 상품입니다.");
        }

        if (this.quantity < quantity) {
            throw new RuntimeException("요청한 수량(" + quantity + ")이 재고(" + this.quantity + ")보다 많습니다.");
        }

        this.quantity -= quantity;

        if (this.quantity == 0) {
            this.isSoldOut = true;
        }
    }

    public void increaseViewCount(int amount) {
        this.viewCount += amount;
    }
}