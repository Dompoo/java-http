package com.techcourse.servlet_impl;

import com.java.http.Header;
import com.java.http.HttpRequest;
import com.java.http.HttpResponse;
import com.java.http.Session;
import com.java.servlet.Servlet;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.java.http.HttpRequest.HttpMethod.POST;

public class RegisterServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(RegisterServlet.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == POST && request.uri().equals("/register");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> form = request.body().asFormUrlEncoded();
        String account = form.get("account");
        String email = form.get("email");
        String password = form.get("password");
        if (account == null || email == null | password == null) {
            return HttpResponse.redirect("/401.html");
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공, user={}", user);

        Session session = request.session(false);
        if (session != null) session.changeSessionId();
        else session = request.session(true);
        session.setAttribute("user", user);
        return HttpResponse.redirect("/index.html")
                .addHeader(Header.setCookie("JSESSIONID", session.id()));
    }
}
