#ifndef INGREDIENT_H
#define INGREDIENT_H

#include <string>

class Ingredient {
private:
    int id;              // 재료 ID (DB 연동 대비)
    std::string name;    // 재료 이름 (예: "계란")
    double amount;       // 수량 (예: 2.0)
    std::string unit;    // 단위 (예: "개", "ml", "g")

public:
    // 생성자
    Ingredient();
    Ingredient(int id, const std::string& name, double amount, const std::string& unit);

    // Getter
    int getId() const;
    std::string getName() const;
    double getAmount() const;
    std::string getUnit() const;

    // Setter
    void setId(int id);
    void setName(const std::string& name);
    void setAmount(double amount);
    void setUnit(const std::string& unit);
};

#endif // INGREDIENT_H
