package com.recipe.domain.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeWriteRequestDTO {
    
    private String title;           // 레시피 제목
    private String introduction;    // 레시피 소개
    private String videoUrl;        // 동영상 URL
    private String tags;            // 태그
    private List<Ingredient> ingredients;  // 재료 목록
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ingredient {
        private String name;    // 재료명
        private String amount;  // 양
        private String unit;    // 단위
        private String note;    // 비고
    }
}