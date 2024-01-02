package org.example.server;

import org.example.domain.MessageHandler;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class ClientRunnableTask implements Runnable{

    private final SocketWrapper currentClient;
    private final Map<Long, SocketWrapper> clients;

    public ClientRunnableTask(Map<Long, SocketWrapper>  clients, SocketWrapper currentClient) {
        this.currentClient = currentClient;
        this.clients = clients;
    }

    @Override
    public void run() {
        try (Scanner input = currentClient.getInput(); PrintWriter output = currentClient.getOutput()) {
            output.println(MessageHandler.setupIdMessage(currentClient.getId()));
            while (true) {
                String clientInput = input.nextLine();
                if (MessageHandler.isNameExchange(clientInput)) {
                    String name = MessageHandler.getValue(clientInput);
                    currentClient.setName(name);
                    System.out.printf("Клиент с Именем %s зарегистрировался\n", name);
                    clients.values().forEach(client -> {
                        if(client.getId()!=currentClient.getId()) {
                            String message = MessageHandler.setupMessage(String.format("Клиент с Именем %s и id %s зарегистрировался\n", currentClient.getName(), currentClient.getId()));
                            client.getOutput().println(message);
                        }
                    });
                }

                if(MessageHandler.isMessageTo(clientInput)) {
                    System.out.println(MessageHandler.getValue(clientInput));                }


                if (Objects.equals("q", clientInput)) {
                    // todo разослать это сообщение всем остальным клиентам
                    clients.remove(currentClient.getId());
                    clients.values().forEach(it -> it.getOutput().println("Клиент[" + currentClient.getId() + "] отключился"));
                    break;
                }

                // формат сообщения: "цифра сообщение"
                //long destinationId = Long.parseLong(clientInput.substring(0, 1));
                //SocketWrapper destination = clients.get(destinationId);
                //destination.getOutput().println(clientInput);
            }
        }
    }
}
