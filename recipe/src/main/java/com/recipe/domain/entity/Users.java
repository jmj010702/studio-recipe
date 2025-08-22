package com.recipe.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
//TODO DTO에 NOT NULL을 사용할지
public class Users {
    @Id@GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true) //nullable = false은 SQL 쿼리를 보내는 시점에 예외 발생 -> @NotNull 사용
    private String id;
    private String pwd;
    private String name;
    @Column(unique = true)
    private String nickname;
    @Column(unique = true)
    private String email;
    private short age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "create_date")
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createDate;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @Column(name=  "modified_date")
    private LocalDateTime modifiedDate;
}
