#include <iostream>
#include <mysql/jdbc.h>   // MySQL Connector/C++ ���

using namespace std;

int main() {
    try {
        // ����̹� ��������
        sql::mysql::MySQL_Driver* driver;
        sql::Connection* con;
        sql::Statement* stmt;
        sql::ResultSet* res;

        driver = sql::mysql::get_mysql_driver_instance();
        con = driver->connect("tcp://127.0.0.1:3306", "root", "your_password");

        // DB ����
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
