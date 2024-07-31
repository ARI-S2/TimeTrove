package com.timetrove.Project.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.timetrove.Project.domain.Board;
import com.timetrove.Project.domain.User;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@Entity
@NoArgsConstructor
@Table(name = "comment", indexes = {
	    @Index(name = "idx_user_code", columnList = "user_code"),
	    @Index(name = "idx_no", columnList = "no"),
	    @Index(name = "idx_parent_id", columnList = "parent_id")
	})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id; // 댓글 고유 번호
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code")
    private User user; // 댓글 작성자
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "no")
    private Board board;
    
    @Column(nullable = false)
    @Lob
    private String content; // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private String createdAt;
    
    
    public void setContent(String content) {
		this.content = content;
	}

	@Builder
    public Comment(Board board, String content, User user, Comment parent) {
        this.board = board;
        this.content = content;
        this.user = user;
        this.parent = parent;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
