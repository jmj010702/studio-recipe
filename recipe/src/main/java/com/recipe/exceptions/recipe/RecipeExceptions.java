package com.recipe.exceptions.recipe;

import lombok.Getter;

@Getter
public enum RecipeExceptions {
    NOT_FOUND("NOT_FOUND", 404),
    BAD_REQUEST("BAD_REQUEST", 400);

    private RecipeException recipeException;

    RecipeExceptions(String massage, int code){
        recipeException = new RecipeException(massage, code);
    }
    
    public RecipeException getRecipeException(){
        return recipeException;
    }
}
