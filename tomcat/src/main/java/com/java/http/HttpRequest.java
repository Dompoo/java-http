package com.java.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public record HttpRequest(
        StartLine startLine,
        Headers headers
) {
    public static HttpRequest from(InputStream inputStream) throws IOException {
        List<String> strings = readInputStream(inputStream);

        StartLine startLine = StartLine.from(strings.getFirst());
        Headers headers = Headers.from(strings.subList(1, strings.size() - 1));

        return new HttpRequest(startLine, headers);
    }

    private static List<String> readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        List<String> result = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
            if (line.isEmpty()) {
                break;
            }
        }

        return result;
    }

    public String param(String key) {
        return startLine.queryParameter().get(key);
    }

    public HttpMethod method() {
        return startLine.method();
    }

    public String uri() {
        return startLine.uri();
    }

    private record StartLine(
            HttpMethod method,
            String uri,
            Map<String, String> queryParameter
    ) {
        public static StartLine from(String startLine) {
            String[] requestLine = startLine.split(" ");

            if (!requestLine[1].contains("?")) {
                String method = requestLine[0];
                String uri = requestLine[1];
                return new StartLine(HttpMethod.parse(method), uri, Collections.emptyMap());
            } else {
                String method = requestLine[0];
                String[] uriAndParams = requestLine[1].split("\\?");
                String uri = uriAndParams[0];
                String params = uriAndParams[1];

                Map<String, String> paramMap = Arrays.stream(params.split("&"))
                        .map(param -> param.split("="))
                        .collect(Collectors.toMap(param -> param[0], param -> param[1]));
                return new StartLine(HttpMethod.parse(method), uri, Collections.unmodifiableMap(paramMap));
            }
        }
    }

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        OPTIONS,
        ;

        public static HttpMethod parse(String str) {
            return Arrays.stream(HttpMethod.values())
                    .filter(value -> value.name().equalsIgnoreCase(str))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP 메서드입니다. input=" + str));
        }
    }
}
