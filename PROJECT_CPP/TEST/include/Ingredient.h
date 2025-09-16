#ifndef INGREDIENT_H
#define INGREDIENT_H

#include <string>

class Ingredient {
private:
    int id;              // ��� ID (DB ���� ���)
    std::string name;    // ��� �̸� (��: "���")
    double amount;       // ���� (��: 2.0)
    std::string unit;    // ���� (��: "��", "ml", "g")

public:
    // ������
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
