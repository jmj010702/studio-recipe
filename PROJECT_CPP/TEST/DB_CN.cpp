#include <iostream>
#include <mysql/jdbc.h>   // MySQL Connector/C++ 헤더

using namespace std;

int main() {
    try {
        // 드라이버 가져오기
        sql::mysql::MySQL_Driver* driver;
        sql::Connection* con;
        sql::Statement* stmt;
        sql::ResultSet* res;

        driver = sql::mysql::get_mysql_driver_instance();
        con = driver->connect("tcp://127.0.0.1:3306", "root", "your_password");

        // DB 선택
        con->setSchema("recipes_db");

        stmt = con->createStatement();
        res = stmt->executeQuery("SELECT recipe_id, recipe_name FROM recipes LIMIT 10");

        while (res->next()) {
            cout << res->getInt("recipe_id") << " | " << res->getString("recipe_name") << endl;
        }

        delete res;
        delete stmt;
        delete con;
    }
    catch (sql::SQLException& e) {
        cout << "MySQL error: " << e.what() << endl;    
    }
    return 0;
}
