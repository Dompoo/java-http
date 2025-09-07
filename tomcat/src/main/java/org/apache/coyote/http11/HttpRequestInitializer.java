package org.apache.coyote.http11;

import com.java.http.HttpRequest;
import com.java.http.SessionStore;
import org.apache.catalina.session.SessionManager;

import java.io.IOException;
import java.io.InputStream;

public class HttpRequestInitializer {

    public static HttpRequest init(InputStream inputStream) throws IOException {
        HttpRequest request = HttpRequest.from(inputStream);
        SessionStore sessionStore = SessionManager.getSessionStore();
        String sessionId = request.cookie("JSESSIONID");
        if (sessionId != null && sessionStore.get(sessionId) != null) {
            request.setSession(sessionStore.get(sessionId));
        } else {
            request.setSession(sessionStore.create());
        }
        return request;
    }
}
