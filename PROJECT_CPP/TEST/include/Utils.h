#ifndef UTILS_H
#define UTILS_H

#include <string>
#include <vector>

namespace Utils {

    // ���ڿ� �ڸ��� (delimiter ����)
    std::vector<std::string> split(const std::string& str, char delimiter);

    // ���ڿ� �յ� ���� ����
    std::string trim(const std::string& str);

    // ���ڿ� �ҹ��ڷ� ��ȯ
    std::string toLower(const std::string& str);

    // CSV ������ 2���� ���ͷ� �ε�
    // ��: [["1", "��ġ�", "��ġ, �������, �κ�"], ["2", "�����", "����, ����, ����"]]
    std::vector<std::vector<std::string>> loadCSV(const std::string& filename);

}

#endif // UTILS_H
