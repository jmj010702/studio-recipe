#include "Algorithm.h"
#include "Utils.h"
#include <algorithm>
#include <iostream>

// ������ ���� ���
int Algorithm::score(const Recipe& recipe, const std::vector<std::string>& fridgeIngredients) {
    int matchCount = 0;
    int missingCount = 0;

    // ����� ��Ḧ �ҹ��ڷ� ��ȯ
    std::vector<std::string> fridgeLower;
    for (const auto& item : fridgeIngredients) {
        fridgeLower.push_back(Utils::toLower(item));
    }

    // ������ ���� ��
    for (const auto& ing : recipe.getIngredients()) {
        std::string ingName = Utils::toLower(ing.getName());
        if (std::find(fridgeLower.begin(), fridgeLower.end(), ingName) != fridgeLower.end()) {
            matchCount++;
        }
        else {
            missingCount++;
        }
    }

    // ���� ���: ���Ե� ��� 10��, ���� ��� -5��
    int totalScore = matchCount * 10 - missingCount * 5;
    return totalScore;
}

// ��õ �˰���
std::vector<Recipe> Algorithm::recommend(
    const std::vector<Recipe>& recipes,
    const std::vector<std::string>& fridgeIngredients,
    int topN
) {
    // ������ + ���� �� ����
    std::vector<std::pair<Recipe, int>> scoredRecipes;

    for (const auto& r : recipes) {
        int s = score(r, fridgeIngredients);
        scoredRecipes.push_back({ r, s });
    }

    // ���� �������� ����
    std::sort(scoredRecipes.begin(), scoredRecipes.end(),
        [](const std::pair<Recipe, int>& a, const std::pair<Recipe, int>& b) {
            return a.second > b.second;
        });

    // ���� N�� ����
    std::vector<Recipe> topRecipes;
    for (int i = 0; i < topN && i < scoredRecipes.size(); i++) {
        topRecipes.push_back(scoredRecipes[i].first);
    }

    return topRecipes;
}
