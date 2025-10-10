#ifndef RECIPE_H
#define RECIPE_H

#include <string>
#include <vector>
#include "Ingredient.h"

class Recipe {
private:
    int id;                          // 레시피 ID
    std::string name;                // 레시피 이름
    std::vector<Ingredient> ingredients; // 필요한 재료 목록
    std::string instructions;        // 조리 방법
    int time;                        // 조리 시간 (분 단위)
    int difficulty;                  // 난이도 (1=쉬움 ~ 5=어려움)

public:
    // 생성자
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

    // 기능
    void addIngredient(const Ingredient& ingredient); // 재료 추가
    bool hasIngredient(const std::string& ingredientName) const; // 특정 재료 포함 여부
};

#endif // RECIPE_H
