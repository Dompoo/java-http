package com.java.http;

public interface Session {

    String id();

    Object attributeOf(String name);

    void setAttribute(String name, Object value);

    void changeSessionId();
}
