#include "Algorithm.h"
#include "Utils.h"
#include <algorithm>
#include <iostream>

// 레시피 점수 계산
int Algorithm::score(const Recipe& recipe, const std::vector<std::string>& fridgeIngredients) {
    int matchCount = 0;
    int missingCount = 0;

    // 냉장고 재료를 소문자로 변환
    std::vector<std::string> fridgeLower;
    for (const auto& item : fridgeIngredients) {
        fridgeLower.push_back(Utils::toLower(item));
    }

    // 레시피 재료와 비교
    for (const auto& ing : recipe.getIngredients()) {
        std::string ingName = Utils::toLower(ing.getName());
        if (std::find(fridgeLower.begin(), fridgeLower.end(), ingName) != fridgeLower.end()) {
            matchCount++;
        }
        else {
            missingCount++;
        }
    }

    // 점수 계산: 포함된 재료 10점, 없는 재료 -5점
    int totalScore = matchCount * 10 - missingCount * 5;
    return totalScore;
}

// 추천 알고리즘
std::vector<Recipe> Algorithm::recommend(
    const std::vector<Recipe>& recipes,
    const std::vector<std::string>& fridgeIngredients,
    int topN
) {
    // 레시피 + 점수 쌍 저장
    std::vector<std::pair<Recipe, int>> scoredRecipes;

    for (const auto& r : recipes) {
        int s = score(r, fridgeIngredients);
        scoredRecipes.push_back({ r, s });
    }

    // 점수 내림차순 정렬
    std::sort(scoredRecipes.begin(), scoredRecipes.end(),
        [](const std::pair<Recipe, int>& a, const std::pair<Recipe, int>& b) {
            return a.second > b.second;
        });

    // 상위 N개 선택
    std::vector<Recipe> topRecipes;
    for (int i = 0; i < topN && i < scoredRecipes.size(); i++) {
        topRecipes.push_back(scoredRecipes[i].first);
    }

    return topRecipes;
}
