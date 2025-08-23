package com.recipe.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.recipe.domain.entity.enums.PreferenceType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@ToString
public class UserReference {
    @Id
    @GeneratedValue
    @Column(name = "preference_id")
    private Long preferenceId;

    @Column(name = "preference_type")
    @Enumerated(EnumType.STRING)
    private PreferenceType preference;


    //RATING


    @Column(name = "preference_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    @CreatedDate
    @LastModifiedDate
    private LocalDateTime date;
}
