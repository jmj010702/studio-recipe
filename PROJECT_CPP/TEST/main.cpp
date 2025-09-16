#include <iostream>
#include "RecipeManager.h"
#include "Algorithm.h"

int main() {
    RecipeManager manager;

    // CSV ���� �ε�
    manager.loadCSV("data/TB_RECIPE_SEARCH_241226.csv");
    std::cout << "�� ������ ����: " << manager.size() << std::endl;

    // ����� ��� �Է� (����� ����)
    std::vector<std::string> fridge = { "���", "����", "����" };

    // ��õ ����
    std::vector<Recipe> results = Algorithm::recommend(manager.getRecipes(), fridge, 5);

    // ��� ���
    std::cout << "��õ ������ TOP 5:" << std::endl;
    for (auto& r : results) {
        std::cout << "- " << r.getName() << std::endl;
    }

    return 0;
}
