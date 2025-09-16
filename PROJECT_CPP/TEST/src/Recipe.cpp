#include "Recipe.h"
#include <algorithm>

// 기본 생성자
Recipe::Recipe() : id(0), name(""), instructions(""), time(0), difficulty(0) {}

// 매개변수 생성자
Recipe::Recipe(int id, const std::string& name) : id(id), name(name), instructions(""), time(0), difficulty(0) {}

// Getter
int Recipe::getId() const { return id; }
std::string Recipe::getName() const { return name; }
std::vector<Ingredient> Recipe::getIngredients() const { return ingredients; }
std::string Recipe::getInstructions() const { return instructions; }
int Recipe::getTime() const { return time; }
int Recipe::getDifficulty() const { return difficulty; }

// Setter
void Recipe::setId(int id) { this->id = id; }
void Recipe::setName(const std::string& name) { this->name = name; }
void Recipe::setIngredients(const std::vector<Ingredient>& ingredients) { this->ingredients = ingredients; }
void Recipe::setInstructions(const std::string& instructions) { this->instructions = instructions; }
void Recipe::setTime(int time) { this->time = time; }
void Recipe::setDifficulty(int difficulty) { this->difficulty = difficulty; }

// 기능
void Recipe::addIngredient(const Ingredient& ingredient) {
    ingredients.push_back(ingredient);
}

bool Recipe::hasIngredient(const std::string& ingredientName) const {
    for (const auto& ing : ingredients) {
        if (ing.getName() == ingredientName) {
            return true;
        }
    }
    return false;
}
