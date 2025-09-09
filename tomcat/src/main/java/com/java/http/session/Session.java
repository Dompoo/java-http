package com.java.http.session;

public interface Session {

    String id();

    Object attributeOf(String name);

    void setAttribute(String name, Object value);

    void changeSessionId();
}
