#ifndef RECIPEMANAGER_H
#define RECIPEMANAGER_H

#include <string>
#include <vector>
#include "Recipe.h"

class RecipeManager {
private:
    std::vector<Recipe> recipes;   // ������ ��� ����

public:
    // CSV ���� �ε�
    void loadCSV(const std::string& filename);

    // ��ü ������ ��ȯ
    const std::vector<Recipe>& getRecipes() const;

    // Ư�� ��� �̸��� ���Ե� ������ �˻�
    std::vector<Recipe> findByIngredient(const std::string& ingredientName) const;

    // Ư�� ������ ID�� �˻�
    Recipe* findById(int id);

    // ������ �� Ȯ��
    size_t size() const;
};

#endif // RECIPEMANAGER_H
