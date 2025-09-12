package com.techcourse.servlet_impl;

import com.java.http.request_response.Header;
import com.java.http.request_response.HttpRequest;
import com.java.http.request_response.HttpResponse;
import com.java.http.session.Session;
import com.java.servlet.Servlet;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.container.TomcatServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static com.java.http.request_response.HttpMethod.POST;

@TomcatServlet
public class LoginServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == POST && request.uri().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> form = request.body().asFormUrlEncoded();
        String account = form.get("account");
        String password = form.get("password");
        if (account == null || password == null) {
            return HttpResponse.redirect("/401.html");
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return HttpResponse.redirect("/401.html");
        }
        log.info("로그인 성공, user={}", user.get());

        Session session = request.session(false);
        if (session != null) request.changeSessionId();
        else session = request.session(true);
        session.setAttribute("user", user.get());
        return HttpResponse.redirect("/index.html")
                .addHeader(Header.setCookie("JSESSIONID", session.id()));
    }
}
