package com.techcourse.servlet_impl;

import com.java.http.Body;
import com.java.http.HttpRequest;
import com.java.http.HttpResponse;
import com.java.http.Session;
import com.java.servlet.Servlet;

import static com.java.http.HttpRequest.HttpMethod.GET;

public class LoginPageServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Session session = request.session();
        Object loginUser = session.attributeOf("loginUser");
        if (loginUser != null) {
            return HttpResponse.redirect("/index.html");
        }

        return HttpResponse.ok().body(Body.resource("static/login.html"));
    }
}
