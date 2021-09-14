package ru.kpfu.itis.garipov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpClientManager implements HttpClient {
    @Override
    public String get(String url, Map<String, String> headers, Map<String, String> params) {
        StringBuilder content = new StringBuilder();
        try {
            URL getUrl = new URL(getUrlWithParams(url, params));
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

            setRequestProperty(connection, headers);

            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            )) {
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                }
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    @Override
    public String post(String url, Map<String, String> headers, Map<String, String> params) {
        StringBuilder content = new StringBuilder();
        try {
            URL postUrl = new URL(url);
            HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();

            postConnection.setRequestMethod("POST");

            setRequestProperty(postConnection, headers);

            postConnection.setDoOutput(true);

            String jsonInputString = getJsonInputString(params);

            try (OutputStream outputStream = postConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(postConnection.getInputStream(), StandardCharsets.UTF_8)
                         )
            ) {
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input.trim());
                }
            }

            postConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public String getUrlWithParams(String url, Map<String, String> params) {
        StringBuilder urlWithParams = new StringBuilder();
        urlWithParams.append(url);
        urlWithParams.append("?");
        Set<String> keysOfParams = params.keySet();
        for (String key : keysOfParams) {
            urlWithParams.append(key);
            urlWithParams.append("=");
            urlWithParams.append(params.get(key));
            urlWithParams.append("&");
        }
        urlWithParams.delete(urlWithParams.length() - 1, urlWithParams.length());
        return urlWithParams.toString();
    }

    public String getJsonInputString(Map<String, String> params) {
        StringBuilder jsonInputString = new StringBuilder();
        jsonInputString.append("{");
        Set<String> keysOfParams = params.keySet();
        for (String key : keysOfParams) {
            jsonInputString.append("\"");
            jsonInputString.append(key);
            jsonInputString.append("\"");
            jsonInputString.append(":");
            jsonInputString.append("\"");
            jsonInputString.append(params.get(key));
            jsonInputString.append("\"");
            jsonInputString.append(":");
            jsonInputString.append(", ");
        }
        jsonInputString.delete(jsonInputString.length() - 2, jsonInputString.length());
        jsonInputString.append("}");
        return jsonInputString.toString();
    }

    public void setRequestProperty(HttpURLConnection connection, Map<String, String> headers) {
        Set<String> keysOfHeaders = headers.keySet();
        for (String key : keysOfHeaders) {
            connection.setRequestProperty(key, headers.get(key));
        }
    }

    public static void main(String[] args) {
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        params.put("I","You");
        params.put("Hello","world");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        HttpClientManager httpClientManager = new HttpClientManager();
        System.out.println(httpClientManager.get("https://postman-echo.com/get", headers, params));
        System.out.println(httpClientManager.post("https://postman-echo.com/post", headers, params));
    }
}
