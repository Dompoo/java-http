package com.techcourse.servlet_impl;

import com.java.http.Header;
import com.java.http.HttpRequest;
import com.java.http.HttpResponse;
import com.java.servlet.Servlet;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.java.http.HttpRequest.HttpMethod.POST;

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
        HttpResponse response = HttpResponse.redirect("/index.html");
        if (request.cookie("JSESSIONID") == null) {
            response.addHeader(Header.setCookie("JSESSIONID", UUID.randomUUID().toString()));
        }
        return response;
    }
}
