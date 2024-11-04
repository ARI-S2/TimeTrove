package com.timetrove.Project.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_timetrove", indexes = {
	    @Index(name = "idx_kakao_nickname", columnList = "kakao_nickname")
	})
public class User {
    @Id
    @Column(name = "user_code")
    private Long userCode;

    @Column(name = "kakao_profile_img")
    private String kakaoProfileImg;

    @Column(name = "kakao_nickname")
    private String kakaoNickname;

    @Column(name = "user_role")
    private String userRole;

    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    @Builder
    public User(Long userCode, String kakaoProfileImg, String kakaoNickname, String userRole) {

        this.userCode = userCode;
        this.kakaoProfileImg = kakaoProfileImg;
        this.kakaoNickname = kakaoNickname;
        this.userRole = userRole;
    }

}
