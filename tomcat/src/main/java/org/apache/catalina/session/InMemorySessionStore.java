package org.apache.catalina.session;

import com.java.http.Session;
import com.java.http.SessionStore;

import java.util.HashMap;
import java.util.Map;

public class InMemorySessionStore implements SessionStore {

    private final Map<String, Session> sessions = new HashMap<>();

    @Override
    public Session create() {
        Session session = new SimpleSession();
        sessions.put(session.id(), session);
        return session;
    }

    @Override
    public Session get(String sessionId) {
        return sessions.get(sessionId);
    }
}
