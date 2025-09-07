package com.java.http;

public class SessionManager {

    private static SessionStore sessionStore;

    public static void setSessionStore(SessionStore sessionStore) {
        SessionManager.sessionStore = sessionStore;
    }

    public static SessionStore getSessionStore() {
        return sessionStore;
    }
}
