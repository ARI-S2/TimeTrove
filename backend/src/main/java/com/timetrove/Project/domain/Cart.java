package com.timetrove.Project.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_code", referencedColumnName = "user_code")
    private User user;

    @ManyToOne
    @JoinColumn(name = "watch_id", referencedColumnName = "watch_id")
    private Watch watch;

    private Long quantity;
    private boolean purchased;
    
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Builder
    public Cart(User user, Watch watch, Long quantity, boolean purchased, LocalDateTime purchaseDate) {
        this.user = user;
        this.watch = watch;
        this.quantity = quantity;
        this.purchased = purchased;
        this.purchaseDate = purchaseDate;
    }
}