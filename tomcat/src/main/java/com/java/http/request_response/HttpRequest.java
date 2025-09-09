package com.java.http.request_response;

import com.java.http.session.Session;
import com.java.http.session.SessionManager;
import com.java.http.session.SessionStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public record HttpRequest(
        StartLine startLine,
        Headers headers,
        Body body
) {

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        List<String> startLineAndHeaders = readUntilNewLine(reader);
        StartLine startLine = StartLine.from(startLineAndHeaders.getFirst());
        // TODO : 헤더가 없는 경우 처리
        Headers headers = Headers.from(startLineAndHeaders.subList(1, startLineAndHeaders.size() - 1));

        Body body = Body.EMPTY;
        String contentLength = headers.valueOf("Content-Length");
        if (contentLength != null) {
            String bodyValue = readBy(reader, Integer.parseInt(contentLength));
            body = Body.plaintext(bodyValue);
        }

        return new HttpRequest(startLine, headers, body);
    }

    private static List<String> readUntilNewLine(BufferedReader reader) throws IOException {
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

    private static String readBy(BufferedReader reader, int contentLength) throws IOException {
        char[] bodyChars = new char[contentLength];
        int read = reader.read(bodyChars, 0, contentLength);
        return new String(bodyChars, 0, read);
    }

    public HttpMethod method() {
        return startLine.method();
    }

    public String uri() {
        return startLine.uri();
    }

    public String param(String key) {
        return startLine.queryParameter().get(key);
    }

    public String cookie(String key) {
        return headers.cookies().get(key);
    }

    public Session session(boolean create) {
        SessionStore sessionStore = SessionManager.getSessionStore();
        if (create) {
            return sessionStore.create();
        }
        String sessionId = cookie("JSESSIONID");
        return sessionStore.get(sessionId);
    }

    public void changeSessionId() {
        SessionStore sessionStore = SessionManager.getSessionStore();
        Session session = session(false);
        sessionStore.changeSessionId(session);
    }

    private record StartLine(
            HttpMethod method,
            String uri,
            Map<String, String> queryParameter
    ) {
        public static StartLine from(String startLine) {
            String[] methodAndUri = startLine.split(" ");

            String method = methodAndUri[0];
            if (!methodAndUri[1].contains("?")) {
                String uri = methodAndUri[1];
                return new StartLine(HttpMethod.parse(method), uri, Collections.emptyMap());
            } else {
                String[] uriAndParams = methodAndUri[1].split("\\?");
                String uri = uriAndParams[0];
                String params = uriAndParams[1];

                Map<String, String> paramMap = Arrays.stream(params.split("&"))
                        .map(param -> param.split("="))
                        .filter(param -> param.length == 2)
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
                    .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 HTTP 메서드입니다. input=" + str));
        }
    }
}
