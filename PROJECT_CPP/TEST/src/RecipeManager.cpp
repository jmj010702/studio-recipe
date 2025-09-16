#include "RecipeManager.h"
#include "Utils.h"
#include <iostream>
#include <sstream>

// CSV 로드
void RecipeManager::loadCSV(const std::string& filename) {
    recipes.clear();

    try {
        // CSV 전체 로드
        std::vector<std::vector<std::string>> data = Utils::loadCSV(filename);

        for (const auto& row : data) {
            if (row.size() < 3) continue; // 최소 컬럼: ID, 이름, 재료

            // 레시피 기본 정보
            int id = std::stoi(row[0]);
            std::string name = row[1];

            Recipe recipe(id, name);

            // 재료 처리 (row[2]에 재료가 ','로 구분되어 있다고 가정)
            std::vector<std::string> ingredientsStr = Utils::split(row[2], '/'); // 예: "계란/우유/양파"
            for (const auto& ingName : ingredientsStr) {
                std::string trimmedName = Utils::trim(ingName);
                if (!trimmedName.empty()) {
                    Ingredient ing(0, trimmedName, 0, ""); // ID, 수량, 단위는 일단 0/빈 문자열
                    recipe.addIngredient(ing);
                }
            }

            // 옵션 컬럼: 조리법, 시간, 난이도
            if (row.size() >= 4) recipe.setInstructions(row[3]);
            if (row.size() >= 5) recipe.setTime(std::stoi(row[4]));
            if (row.size() >= 6) recipe.setDifficulty(std::stoi(row[5]));

            recipes.push_back(recipe);
        }

    }
    catch (const std::exception& e) {
        std::cerr << "CSV 로드 오류: " << e.what() << std::endl;
    }
}

// 전체 레시피 반환
const std::vector<Recipe>& RecipeManager::getRecipes() const {
    return recipes;
}

// 특정 재료 포함 레시피 검색
std::vector<Recipe> RecipeManager::findByIngredient(const std::string& ingredientName) const {
    std::vector<Recipe> result;
    for (const auto& recipe : recipes) {
        if (recipe.hasIngredient(ingredientName)) {
            result.push_back(recipe);
        }
    }
    return result;
}

// 특정 ID로 레시피 검색
Recipe* RecipeManager::findById(int id) {
    for (auto& recipe : recipes) {
        if (recipe.getId() == id) return &recipe;
    }
    return nullptr;
}

// 레시피 수 반환
size_t RecipeManager::size() const {
    return recipes.size();
}
