package org.apache.catalina.connector;

import com.java.http.session.SessionManager;
import org.apache.catalina.container.Container;
import org.apache.catalina.container.SimpleContainer;
import org.apache.catalina.session.InMemorySessionStore;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREAD = 10;
    private static final Container DEFAULT_CONTAINER = new SimpleContainer();

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Container container;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD, DEFAULT_CONTAINER);
    }

    public Connector(final int port, final int acceptCount, final int maxThread, final Container container) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executorService = Executors.newFixedThreadPool(maxThread);
        this.container = container;
        this.stopped = false;
        SessionManager.setSessionStore(new InMemorySessionStore());
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start() {
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection==null) {
            return;
        }
        var processor = new Http11Processor(connection, container);
        executorService.execute(processor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int checkPort(final int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(final int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
