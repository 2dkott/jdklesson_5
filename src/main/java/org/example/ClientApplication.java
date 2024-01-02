package org.example;

import lombok.Getter;
import org.example.domain.MessageHandler;
import org.example.server.ServerApplication;
import org.h2.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;


public class ClientApplication {

    static Scanner consoleScanner = new Scanner(System.in);
    static ClientSocket client;

    public static void main(String[] args) throws IOException {
        String clientName = getName();
        if (StringUtils.isNullOrEmpty(clientName)) {
            System.out.println("Кол-во попыток задать Имя пользователя закончилось! Выход\n");
            return;
        }
        client = new ClientSocket(new Socket("localhost", ServerApplication.PORT), clientName);
        //serverExchange(client);

        new Thread(() -> {
            try (Scanner input = new Scanner(client.getSocket().getInputStream())) {
                while (true) {
                    String message = input.nextLine();
                    //if(MessageHandler.isIdExchange(message)) {clientSocket.setId(MessageHandler.getId(message)); break;};
                    if(MessageHandler.isIdExchange(message)) client.setId(MessageHandler.getId(message));
                    System.out.println(input.nextLine());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        // запись
        new Thread(() -> {
            try (PrintWriter output = new PrintWriter(client.getSocket().getOutputStream(), true)) {
                output.println(MessageHandler.setupCLientName(client.getName()));
                while (true) {
                    System.out.println("Пошлите сообщение(для конкретного пользоватеял используйте @<id>):");
                    String consoleInput = consoleScanner.nextLine();

                    output.println(consoleInput);
                    if (Objects.equals("q", consoleInput)) {
                        client.getSocket().close();
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    static void serverExchange(ClientSocket clientSocket){

        try (Scanner input = new Scanner(clientSocket.getSocket().getInputStream()); PrintWriter output = new PrintWriter(clientSocket.getSocket().getOutputStream(), true)) {
            output.println(MessageHandler.setupCLientName(clientSocket.getName()));
            while (true) {
                String message = input.nextLine();
                if(MessageHandler.isIdExchange(message)) {clientSocket.setId(MessageHandler.getId(message)); break;};
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String getName() {
        System.out.println("Введите Имя пользователя:\n");
        int trys = 0;
        while (trys<5) {
            String input = consoleScanner.nextLine();
            if (input.isEmpty()) {
                System.out.println("Имя пользователя пустое! Попробуйте еще раз\n");
                trys++;
            } else {
                return input;
            }
        }
        return null;
    }
}

@Getter
class ClientSocket {
    final private Socket socket;
    final private String name;
    private Long id;

    public ClientSocket(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
