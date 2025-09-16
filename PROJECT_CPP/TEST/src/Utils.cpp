#include "Utils.h"
#include <fstream>
#include <sstream>
#include <algorithm>
#include <cctype>

namespace Utils {

    // 문자열 자르기 (delimiter 기준)
    std::vector<std::string> split(const std::string& str, char delimiter) {
        std::vector<std::string> tokens;
        std::stringstream ss(str);
        std::string token;
        while (std::getline(ss, token, delimiter)) {
            tokens.push_back(token);
        }
        return tokens;
    }

    // 문자열 앞뒤 공백 제거
    std::string trim(const std::string& str) {
        size_t start = str.find_first_not_of(" \t\n\r");
        if (start == std::string::npos)
            return "";

        size_t end = str.find_last_not_of(" \t\n\r");
        return str.substr(start, end - start + 1);
    }

    // 문자열 소문자로 변환
    std::string toLower(const std::string& str) {
        std::string result = str;
        std::transform(result.begin(), result.end(), result.begin(),
            [](unsigned char c) { return std::tolower(c); });
        return result;
    }

    // CSV 파일을 2차원 벡터로 로드
    std::vector<std::vector<std::string>> loadCSV(const std::string& filename) {
        std::vector<std::vector<std::string>> data;
        std::ifstream file(filename);
        if (!file.is_open()) {
            throw std::runtime_error("CSV 파일을 열 수 없습니다: " + filename);
        }

        std::string line;
        while (std::getline(file, line)) {
            line = trim(line);
            if (line.empty()) continue;

            std::vector<std::string> row = split(line, ',');
            for (auto& cell : row) {
                cell = trim(cell); // 각 셀 공백 제거
            }
            data.push_back(row);
        }

        file.close();
        return data;
    }

} // namespace Utils
