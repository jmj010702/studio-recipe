package com.recipe.domain.dto.Recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSimpleDTO {
    private Long recipeId;
    private String title;
    private String imageUrl;
    private Integer viewCount;
    private Integer likeCount;
}