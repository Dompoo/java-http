package com.java.servlet;

import com.java.http.request_response.Body;
import com.java.http.request_response.HttpRequest;
import com.java.http.request_response.HttpResponse;

import java.util.HashMap;
import java.util.Map;

import static com.java.http.request_response.HttpMethod.GET;
import static com.java.http.request_response.StatusCode.OK;

public class StaticResourceRegistryServlet implements Servlet {

    private final Map<String, String> uri_resourcePath = new HashMap<>();

    public StaticResourceRegistryServlet register(String uri, String resourcePath) {
        uri_resourcePath.put(uri, resourcePath);
        return this;
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && uri_resourcePath.get(request.uri()) != null;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String resourcePath = uri_resourcePath.get(request.uri());
        return HttpResponse.of(OK).body(Body.resource(resourcePath));
    }
}
