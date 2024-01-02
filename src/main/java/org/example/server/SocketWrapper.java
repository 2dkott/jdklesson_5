package org.example.server;

import lombok.Getter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@Getter
public class SocketWrapper implements AutoCloseable {

    private final long id;
    private final Socket socket;
    private final Scanner input;
    private final PrintWriter output;
    private String name;

    public SocketWrapper(long id, Socket socket) throws IOException {
        this.id = id;
        this.socket = socket;
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }

    @Override
    public String toString() {
        return String.format("%s", socket.getInetAddress().toString());
    }
}
