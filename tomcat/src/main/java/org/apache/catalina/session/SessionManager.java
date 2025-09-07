package org.apache.catalina.session;

import com.java.http.SessionStore;

public class SessionManager {

    private static final SessionStore sessionStore = new InMemorySessionStore();

    public static SessionStore getSessionStore() {
        return sessionStore;
    }
}
