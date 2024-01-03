package org.example.server;

import org.example.domain.MessageData;
import org.example.domain.MessageHandler;

import java.io.PrintWriter;
import java.util.*;

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
                    MessageData messageData = MessageHandler.getMessageAndAddressee(clientInput);
                    String message = MessageHandler.setupMessage(String.format("Клиент с id %s написал Вам сообщение:\n%s\n", messageData.sourceId(), messageData.message()));
                    if(messageData.targetId()==-1L) {
                        clients.values().forEach(client -> {
                            if(client.getId()!=currentClient.getId()) {
                                client.getOutput().println(message);
                            }
                        });
                    } else {
                        SocketWrapper addressee = clients.get(messageData.targetId());
                        if (Objects.nonNull(addressee)) addressee.getOutput().println(message);
                    }
                }

                if (Objects.equals("q", clientInput)) {
                    // todo разослать это сообщение всем остальным клиентам
                    clients.remove(currentClient.getId());
                    clients.values().forEach(it -> it.getOutput().println("Клиент[" + currentClient.getId() + "] отключился"));
                    break;
                }

            }
        }
    }
}
