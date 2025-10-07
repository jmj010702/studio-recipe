#ifndef RECIPEMANAGER_H
#define RECIPEMANAGER_H

#include <string>
#include <vector>
#include "Recipe.h"

class RecipeManager {
private:
    std::vector<Recipe> recipes;   // 레시피 목록 저장

public:
    // CSV 파일 로드
    void loadCSV(const std::string& filename);

    // 전체 레시피 반환
    const std::vector<Recipe>& getRecipes() const;

    // 특정 재료 이름이 포함된 레시피 검색
    std::vector<Recipe> findByIngredient(const std::string& ingredientName) const;

    // 특정 레시피 ID로 검색
    Recipe* findById(int id);

    // 레시피 수 확인
    size_t size() const;
};

#endif // RECIPEMANAGER_H
