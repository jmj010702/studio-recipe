package com.recipe.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder  // ⭐ 이거 추가!
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IngredientDto {

    private Long id;
    private String name;
    private String quantity;
    private String unit;
    private String memo;
}