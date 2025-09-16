#include <iostream>
#include "RecipeManager.h"
#include "Algorithm.h"

int main() {
    RecipeManager manager;

    // CSV 파일 로드
    manager.loadCSV("data/TB_RECIPE_SEARCH_241226.csv");
    std::cout << "총 레시피 개수: " << manager.size() << std::endl;

    // 냉장고 재료 입력 (사용자 가정)
    std::vector<std::string> fridge = { "계란", "우유", "양파" };

    // 추천 실행
    std::vector<Recipe> results = Algorithm::recommend(manager.getRecipes(), fridge, 5);

    // 결과 출력
    std::cout << "추천 레시피 TOP 5:" << std::endl;
    for (auto& r : results) {
        std::cout << "- " << r.getName() << std::endl;
    }

    return 0;
}
