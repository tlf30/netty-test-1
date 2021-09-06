package io.tlf.netty.test.app;

import io.tlf.netty.test.lib.TestServer;

public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            TestServer server = new TestServer();
            try {
                server.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        t.start();

        Test67Server server67 = new Test67Server();
        try {
            server67.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
