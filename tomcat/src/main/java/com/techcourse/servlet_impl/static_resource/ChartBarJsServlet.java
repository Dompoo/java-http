package com.techcourse.servlet_impl.static_resource;

import com.java.servlet.StaticResourceServlet;

public class ChartBarJsServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/assets/chart-bar.js";
    }
}
