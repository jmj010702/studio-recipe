package com.recipe.domain.entity; // ✅ 경로 수정

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@Table(name = "user_recipe_log")
public class UserRecipeLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // ✅ com.recipe.domain.entity.User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe; // ✅ com.recipe.domain.entity.Recipe

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private long timestamp;

    public enum ActionType {
        VIEW, LIKE, FAVORITE
    }
}
