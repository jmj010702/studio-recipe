#include "Ingredient.h"

// �⺻ ������
Ingredient::Ingredient() : id(0), name(""), amount(0.0), unit("") {}

// �Ű����� ������
Ingredient::Ingredient(int id, const std::string& name, double amount, const std::string& unit)
    : id(id), name(name), amount(amount), unit(unit) {
}

// Getter
int Ingredient::getId() const { return id; }
std::string Ingredient::getName() const { return name; }
double Ingredient::getAmount() const { return amount; }
std::string Ingredient::getUnit() const { return unit; }

// Setter
void Ingredient::setId(int id) { this->id = id; }
void Ingredient::setName(const std::string& name) { this->name = name; }
void Ingredient::setAmount(double amount) { this->amount = amount; }
void Ingredient::setUnit(const std::string& unit) { this->unit = unit; }
