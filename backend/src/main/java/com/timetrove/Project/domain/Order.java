package com.timetrove.Project.domain;

import com.timetrove.Project.common.enumType.PayMethod;
import com.timetrove.Project.common.enumType.PaymentStatus;
import com.timetrove.Project.dto.OrderDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId; // PK

    @ManyToOne
    @JoinColumn(name = "user_code", referencedColumnName = "user_code", nullable = false)
    private User user; // 사용자

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "order_name")
    private String ordererName; // 주문자 이름

    @Column(name = "product_names")
    private String productNames; // 주문된 상품 이름들

    @Enumerated(EnumType.STRING)
    PayMethod payMethod; // 결제 방식

    @Column(length = 100, name = "merchant_uid")
    private String merchantUid; // 주문번호

    @Column(name = "total_price")
    private BigDecimal totalPrice; // 가격

    @Column(name = "address")
    private String address; // 주소

    @Column(name = "detail_address")
    private String detailAddress; // 상세주소

    @Column(name = "post_code", length = 100)
    private String postCode; // 우편번호

    @Column(name = "phone_number")
    private String phoneNumber; // 전화번호

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.WAITING_FOR_PAYMENT; // 결제 상태 // 결제 상태

    @OneToMany(mappedBy = "order")
    private List<PaymentHistory> paymentHistories = new ArrayList<>(); // 결제내역과 일대다

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderDay; // 주문시각

    @Builder
    public Order(User user, String ordererName, String productNames, BigDecimal totalPrice, String phoneNumber,
                 String postCode, String address, String detailAddress) {
        this.user = user;
        this.ordererName = ordererName;
        this.productNames = productNames;
        this.totalPrice = totalPrice;
        this.phoneNumber = phoneNumber;
        this.postCode = postCode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.orderDay = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.WAITING_FOR_PAYMENT;
    }

    public void orderConfirm(String merchantUid, OrderDto orderDto) {
        this.merchantUid = merchantUid;
        this.postCode = orderDto.getPostCode();
        this.address = orderDto.getAddress();
        this.detailAddress = orderDto.getDetailAddress();
        this.ordererName = orderDto.getOrdererName();
        this.payMethod = orderDto.getPayMethod();
        this.orderDay = LocalDateTime.now();

    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
