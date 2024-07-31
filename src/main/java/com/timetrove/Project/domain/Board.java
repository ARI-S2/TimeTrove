package com.timetrove.Project.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Entity
@Data
@DynamicUpdate
public class Board {
	@Id
    private Long no;
    private String subject;
    private String content;
    @Column(insertable = true,updatable = false)
    private String regdate;
    
    @PrePersist
    public void regdate() {
    	this.regdate=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}