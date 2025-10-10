#ifndef RECIPE_H
#define RECIPE_H

#include <string>
#include <vector>
#include "Ingredient.h"

class Recipe {
private:
    int id;                          // ������ ID
    std::string name;                // ������ �̸�
    std::vector<Ingredient> ingredients; // �ʿ��� ��� ���
    std::string instructions;        // ���� ���
    int time;                        // ���� �ð� (�� ����)
    int difficulty;                  // ���̵� (1=���� ~ 5=�����)

public:
    // ������
    Recipe();
    Recipe(int id, const std::string& name);

    // Getter
    int getId() const;
    std::string getName() const;
    std::vector<Ingredient> getIngredients() const;
    std::string getInstructions() const;
    int getTime() const;
    int getDifficulty() const;

    // Setter
    void setId(int id);
    void setName(const std::string& name);
    void setIngredients(const std::vector<Ingredient>& ingredients);
    void setInstructions(const std::string& instructions);
    void setTime(int time);
    void setDifficulty(int difficulty);

    // ���
    void addIngredient(const Ingredient& ingredient); // ��� �߰�
    bool hasIngredient(const std::string& ingredientName) const; // Ư�� ��� ���� ����
};

#endif // RECIPE_H
