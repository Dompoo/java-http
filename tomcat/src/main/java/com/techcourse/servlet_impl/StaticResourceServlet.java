package com.techcourse.servlet_impl;

import com.java.servlet.StaticResourceRegistryServlet;
import org.apache.catalina.container.TomcatServlet;

@TomcatServlet
public class StaticResourceServlet extends StaticResourceRegistryServlet {

    public StaticResourceServlet() {
        this
                .register("/", "static/index.html")
                .register("/index.html", "static/index.html")
                .register("/register", "static/register.html")
                .register("/401.html", "static/401.html")
                .register("/400.html", "static/400.html")
                .register("/css/styles.css", "static/css/styles.css")
                .register("/js/scripts.js", "static/js/scripts.js")
                .register("/assets/chart-area.js", "static/assets/chart-area.js")
                .register("/assets/chart-bar.js", "static/assets/chart-bar.js")
                .register("/assets/chart-pie.js", "static/assets/chart-pie.js")
                .register("/favicon.ico", "static/favicon.ico");
    }
}
