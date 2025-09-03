package com.recipe.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.recipe.domain.entity.enums.Gender;
import com.recipe.domain.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class User extends CreateTime{
    @Id@GeneratedValue
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ID",unique = true,nullable = false)
    private String id;
    @Column(name="PWD", nullable = false)
    private String pwd;
    @Column(name="NAME", nullable = false)
    private String name;
    @Column(name="NICKNAME" ,unique = true, nullable = false)
    private String nickname;
    @Column(name="EMAIL",unique = true, nullable = false)
    private String email;
    @Column(name="AGE")
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name="GENDER", nullable = false,
    columnDefinition = "ENUM('F', 'M')")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", columnDefinition = "ENUM('ADMIN', 'GUEST')")
    private Role role;

    @Column(name = "CREATED_AT")
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createDate;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @Column(name=  "MODIFIED_AT")
    private LocalDateTime modifiedDate;
}
