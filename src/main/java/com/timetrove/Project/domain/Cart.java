package com.timetrove.Project.domain;

import java.time.LocalDateTime;

import com.timetrove.Project.domain.User;
import com.timetrove.Project.domain.Watch;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "cart", indexes = {
	    @Index(name = "idx_user_code", columnList = "user_code"),
	    @Index(name = "idx_watch_no", columnList = "watch_no")
	})
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_code", referencedColumnName = "user_code")
    private User user;

    @ManyToOne
    @JoinColumn(name = "watch_no", referencedColumnName = "no")
    private Watch watch;

    private int quantity;
    private boolean purchased;
    
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Builder
    public Cart(User user, Watch watch, int quantity, boolean purchased, LocalDateTime purchaseDate) {
        this.user = user;
        this.watch = watch;
        this.quantity = quantity;
        this.purchased = purchased;
        this.purchaseDate = purchaseDate;
    }
}