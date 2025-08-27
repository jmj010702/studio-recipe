package com.recipe.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "RECIPECOMPLETIONS")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString
@Getter
public class RecipeCompletions {
    @Id
    @GeneratedValue
    @Column(name = "COMPLETION_ID")
    private Long completionId;

    @Column(name = "COMPLETION_DATE")
    @JsonFormat(shape=JsonFormat.Shape.STRING ,pattern = "yyyy.MM.dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime completionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECIPE_ID")
    private Recipe recipe;
}
