package org.apache.catalina.session;

import com.java.http.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleSession implements Session {

    private final String id = UUID.randomUUID().toString();
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    public String id() {
        return id;
    }

    @Override
    public Object attributeOf(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
}
