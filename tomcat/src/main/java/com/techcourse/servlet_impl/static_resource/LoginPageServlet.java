package com.techcourse.servlet_impl.static_resource;

import com.java.servlet.StaticResourceServlet;

public class LoginPageServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/login.html";
    }
}
