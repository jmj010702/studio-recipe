package com.recipe.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDTO {
    
    private String title;           // 레시피 제목
    private String introduction;    // 레시피 소개 (description으로도 사용)
    private String videoUrl;        // 동영상 URL (rcpImgUrl로도 사용)
    private String tags;            // 태그
    private List<IngredientDTO> ingredients;  // 재료 목록
    
    // ✅ 서비스에서 사용하는 별칭 메서드
    public String getDescription() {
        return this.introduction;
    }
    
    public String getRcpImgUrl() {
        return this.videoUrl;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngredientDTO {
        private String name;    // 재료명
        private String amount;  // 양
        private String unit;    // 단위
        private String note;    // 비고
    }
}