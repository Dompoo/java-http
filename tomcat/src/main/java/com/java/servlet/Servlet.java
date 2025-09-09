package com.java.servlet;

import com.java.http.request_response.HttpRequest;
import com.java.http.request_response.HttpResponse;

public interface Servlet {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
