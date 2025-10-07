#ifndef ALGORITHM_H
#define ALGORITHM_H

#include <vector>
#include <string>
#include "Recipe.h"

class Algorithm {
public:
    // ������ ��õ: ����� ��� ���
    static std::vector<Recipe> recommend(
        const std::vector<Recipe>& recipes,
        const std::vector<std::string>& fridgeIngredients,
        int topN = 5   // �⺻������ ���� 5�� ��õ
    );

    // ������ ���� ���
    // ���� ��Ģ ��: ���Ե� ��� ���� * 10�� - ������ ��� ���� * 5��
    static int score(const Recipe& recipe, const std::vector<std::string>& fridgeIngredients);
};

#endif // ALGORITHM_H
