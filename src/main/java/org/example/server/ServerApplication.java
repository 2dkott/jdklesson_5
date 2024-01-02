package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerApplication {

    // message broker (kafka, redis, rabbitmq, ...)
    // client sent letter to broker

    // server sent to SMTP-server

    public static final int PORT = 8181;

    private static long clientIdCounter = 1L;
    private static Map<Long, SocketWrapper> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);
            while (true) {
                final Socket client = server.accept();
                final long clientId = clientIdCounter++;

                SocketWrapper wrapper = new SocketWrapper(clientId, client);
                System.out.println("Подключился новый клиент[" + wrapper + "]");
                clients.put(clientId, wrapper);

                new Thread(new ClientRunnableTask(clients, wrapper)).start();
            }
        }
    }

}