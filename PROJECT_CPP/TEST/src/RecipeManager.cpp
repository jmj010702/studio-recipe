#include "RecipeManager.h"
#include "Utils.h"
#include <iostream>
#include <sstream>

// CSV �ε�
void RecipeManager::loadCSV(const std::string& filename) {
    recipes.clear();

    try {
        // CSV ��ü �ε�
        std::vector<std::vector<std::string>> data = Utils::loadCSV(filename);

        for (const auto& row : data) {
            if (row.size() < 3) continue; // �ּ� �÷�: ID, �̸�, ���

            // ������ �⺻ ����
            int id = std::stoi(row[0]);
            std::string name = row[1];

            Recipe recipe(id, name);

            // ��� ó�� (row[2]�� ��ᰡ ','�� ���еǾ� �ִٰ� ����)
            std::vector<std::string> ingredientsStr = Utils::split(row[2], '/'); // ��: "���/����/����"
            for (const auto& ingName : ingredientsStr) {
                std::string trimmedName = Utils::trim(ingName);
                if (!trimmedName.empty()) {
                    Ingredient ing(0, trimmedName, 0, ""); // ID, ����, ������ �ϴ� 0/�� ���ڿ�
                    recipe.addIngredient(ing);
                }
            }

            // �ɼ� �÷�: ������, �ð�, ���̵�
            if (row.size() >= 4) recipe.setInstructions(row[3]);
            if (row.size() >= 5) recipe.setTime(std::stoi(row[4]));
            if (row.size() >= 6) recipe.setDifficulty(std::stoi(row[5]));

            recipes.push_back(recipe);
        }

    }
    catch (const std::exception& e) {
        std::cerr << "CSV �ε� ����: " << e.what() << std::endl;
    }
}

// ��ü ������ ��ȯ
const std::vector<Recipe>& RecipeManager::getRecipes() const {
    return recipes;
}

// Ư�� ��� ���� ������ �˻�
std::vector<Recipe> RecipeManager::findByIngredient(const std::string& ingredientName) const {
    std::vector<Recipe> result;
    for (const auto& recipe : recipes) {
        if (recipe.hasIngredient(ingredientName)) {
            result.push_back(recipe);
        }
    }
    return result;
}

// Ư�� ID�� ������ �˻�
Recipe* RecipeManager::findById(int id) {
    for (auto& recipe : recipes) {
        if (recipe.getId() == id) return &recipe;
    }
    return nullptr;
}

// ������ �� ��ȯ
size_t RecipeManager::size() const {
    return recipes.size();
}
