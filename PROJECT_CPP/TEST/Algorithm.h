#ifndef ALGORITHM_H
#define ALGORITHM_H

#include <vector>
#include <string>
#include "Recipe.h"

class Algorithm {
public:
    // 레시피 추천: 냉장고 재료 기반
    static std::vector<Recipe> recommend(
        const std::vector<Recipe>& recipes,
        const std::vector<std::string>& fridgeIngredients,
        int topN = 5   // 기본적으로 상위 5개 추천
    );

    // 레시피 점수 계산
    // 점수 규칙 예: 포함된 재료 개수 * 10점 - 부족한 재료 개수 * 5점
    static int score(const Recipe& recipe, const std::vector<std::string>& fridgeIngredients);
};

#endif // ALGORITHM_H
