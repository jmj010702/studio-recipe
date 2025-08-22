package com.recipe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
        @UniqueConstraint(name = "UQ_RECIPE_LIKE", columnNames = {"user_id", "recipe_id"})
        })
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class Likes {
    @Id
    @GeneratedValue
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
