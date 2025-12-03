package com.recipe.domain.dto;  // recipe 제거

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeWriteRequestDTO {
    
    private String title;           
    private String introduction;    
    private String videoUrl;        
    private String tags;            
    private MultipartFile thumbnail; 
    private List<Ingredient> ingredients;  
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ingredient {
        private String name;    
        private String amount;  
        private String unit;    
        private String note;    
    }
}