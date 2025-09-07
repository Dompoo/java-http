package com.java.http;

import org.apache.catalina.session.InMemorySessionStore;

public class SessionManager {

    private static final SessionStore sessionStore = new InMemorySessionStore();

    public static SessionStore getSessionStore() {
        return sessionStore;
    }
}
