package org.apache.catalina.container;

import com.java.http.request_response.Body;
import com.java.http.request_response.HttpRequest;
import com.java.http.request_response.HttpResponse;
import com.java.servlet.Servlet;
import com.java.servlet.StaticResourceRegistryServlet;
import com.techcourse.servlet_impl.LoginPageServlet;
import com.techcourse.servlet_impl.LoginServlet;
import com.techcourse.servlet_impl.RegisterServlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.java.http.request_response.HttpResponse.StatusCode.INTERNAL_SERVER_ERROR;
import static com.java.http.request_response.HttpResponse.StatusCode.NOT_FOUND;

public class SimpleContainer implements Container {

    // TODO : 리플렉션/클래스패스로더 등으로 실제 스캔하여 구현
    private static final List<Servlet> servlets = new ArrayList<>();

    static {
        servlets.add(new LoginServlet());
        servlets.add(new LoginPageServlet());
        servlets.add(new RegisterServlet());
        servlets.add(new StaticResourceRegistryServlet()
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
                .register("/favicon.ico", "static/favicon.ico")
        );
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        Optional<Servlet> servlet = findServletFor(request);

        if (servlet.isEmpty()) {
            return HttpResponse.of(NOT_FOUND)
                    .body(Body.plaintext("해당 요청을 처리할 서블릿을 찾지 못했습니다. uri=" + request.uri()));
        }

        try {
            return servlet.get().handle(request);
        } catch (Exception e) {
            return HttpResponse.of(INTERNAL_SERVER_ERROR)
                    .body(Body.stackTrace(e));
        }
    }

    private static Optional<Servlet> findServletFor(HttpRequest request) {
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(request))
                .findFirst();
    }
}
