package com.recipe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LIKES",
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
    @Column(name = "LIKE_ID")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECIPE_ID")
    private Recipe recipe;
}
