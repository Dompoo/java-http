package com.java.http;

public interface SessionStore {

    Session create();

    Session get(String sessionId);
}
