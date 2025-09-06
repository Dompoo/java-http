package com.techcourse.servlet_impl;

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
            return HttpResponse.redirect("/401.html").build();
//            throw new IllegalArgumentException("계정과 비밀번호는 필수입니다.");
            // TODO : 서블릿에서는 예외 상황에 예외를 던져야 하는 것 아닐까?
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        // .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return HttpResponse.redirect("/401.html").build();
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        log.info("로그인 성공, user={}", user.get());
        HttpResponse.HttpResponseBuilder response = HttpResponse.redirect("/index.html");
        if (request.cookie("JSESSIONID") == null) {
            response.setCookie("JSESSIONID", UUID.randomUUID().toString()).build();
        }
        return response.build();
    }
}
