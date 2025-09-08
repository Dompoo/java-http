package com.java.http.session;

public interface SessionStore {

    Session create();

    Session get(String sessionId);

    void changeSessionId(Session session);
}
