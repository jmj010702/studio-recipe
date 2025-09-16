CURL* curl;
CURLcode res;
string jsonData = R"({"recipes":[{"id":1,"name":"°è¶õººÀ½¹ä"}]})";

curl = curl_easy_init();
if (curl) {
    curl_easy_setopt(curl, CURLOPT_URL, "http://localhost:8080/recommend");
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
    struct curl_slist* headers = NULL;
    headers = curl_slist_append(headers, "Content-Type: application/json");
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

    res = curl_easy_perform(curl);
    curl_easy_cleanup(curl);
}
