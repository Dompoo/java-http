package org.apache.catalina.container;

import com.java.http.request_response.HttpRequest;
import com.java.http.request_response.HttpResponse;

public interface Container {

    HttpResponse service(HttpRequest request);
}
