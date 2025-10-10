#ifndef UTILS_H
#define UTILS_H

#include <string>
#include <vector>

namespace Utils {

    // 문자열 자르기 (delimiter 기준)
    std::vector<std::string> split(const std::string& str, char delimiter);

    // 문자열 앞뒤 공백 제거
    std::string trim(const std::string& str);

    // 문자열 소문자로 변환
    std::string toLower(const std::string& str);

    // CSV 파일을 2차원 벡터로 로드
    // 예: [["1", "김치찌개", "김치, 돼지고기, 두부"], ["2", "된장찌개", "된장, 감자, 버섯"]]
    std::vector<std::vector<std::string>> loadCSV(const std::string& filename);

}

#endif // UTILS_H
