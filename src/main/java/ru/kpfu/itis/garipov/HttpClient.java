package ru.kpfu.itis.garipov;

import java.util.Map;

public interface HttpClient {
    //    https://postman-echo.com/get
    String get(String url, Map<String, String> headers, Map<String, String> params);

    //    https://postman-echo.com/post
    String post(String url, Map<String, String> headers, Map<String, String> params);
}
