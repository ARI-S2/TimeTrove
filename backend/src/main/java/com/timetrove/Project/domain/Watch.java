package com.timetrove.Project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(indexes = { // 상품 검색을 위한 인덱스 생성
        @Index(name = "idx_watch_name", columnList = "name"),
        @Index(name = "idx_watch_model", columnList = "model"),
        @Index(name = "idx_watch_view_count", columnList = "view_count"),
        @Index(name = "idx_watch_sold_price", columnList = "sold_price")
})
public class Watch {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watch_id")
	private Long id;

    @Column(unique = true)
	private String name;
	
	private String image;

    @Column(name = "consumer_price")
	private int consumerPrice;
    @Column(name = "sold_price")
	private Long soldPrice;

    private Long quantity;
	private String points;
	private String discount;
	
	@Column(unique = true)
	private String model;

    @Column(name = "detail_image")
    private String dimagesString;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "cart_count")
    private int cartCount;

    @Column(name = "purchase_count")
    private int purchaseCount;


    public void decreaseQuantity(Long quantity) {
        if (this.quantity < quantity) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        this.quantity -= quantity;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseCartCount() {
        this.cartCount++;
    }

    public void increasePurchaseCount() {
        this.purchaseCount++;
    }

}
