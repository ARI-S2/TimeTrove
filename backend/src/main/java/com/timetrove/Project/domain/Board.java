package com.timetrove.Project.domain;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


@Getter
@NoArgsConstructor
@Entity
public class Board {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    private String subject;

    private String content;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(columnDefinition = "integer default 0")
    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    public void increaseScore() {
        this.score += 1;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }
}