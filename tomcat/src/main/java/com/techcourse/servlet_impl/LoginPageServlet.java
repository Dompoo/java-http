package com.techcourse.servlet_impl;

import com.java.http.request_response.Body;
import com.java.http.request_response.HttpRequest;
import com.java.http.request_response.HttpResponse;
import com.java.http.session.Session;
import com.java.servlet.Servlet;

import static com.java.http.request_response.HttpRequest.HttpMethod.GET;

public class LoginPageServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Session session = request.session(false);
        if (session == null) {
            session = request.session(true);
        }
        Object user = session.attributeOf("user");
        if (user != null) {
            return HttpResponse.redirect("/index.html");
        }

        return HttpResponse.ok().body(Body.resource("static/login.html"));
    }
}
