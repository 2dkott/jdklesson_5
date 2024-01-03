package org.example.domain;

import java.util.HashMap;

public class MessageHandler {

    final static String divider = "~~~";
    public static boolean isIdExchange(String messageBody) {
        String[] messageParts = messageBody.split(divider);
        MessageTypes messageType = MessageTypes.valueOf(messageParts[0]);
        return messageType.equals(MessageTypes.ID_EXCHANGE);
    }

    public static Long getId(String messageBody) {
        String[] messageParts = messageBody.split(divider);
        return Long.valueOf(messageParts[1]);
    }

    public static String setupIdMessage(Long id) {
        return String.format("%s%s%s", MessageTypes.ID_EXCHANGE, divider, id);
    }

    public static String setupMessage(String message) {
        return String.format("%s%s%s", MessageTypes.MESSAGE, divider, message);
    }

    public static String setupMessage(String message, Long targetId, Long sourceId) {
        return String.format("%s%s%s%s%s%s", MessageTypes.MESSAGE, divider, targetId, divider, sourceId, divider, message);
    }

    public static String setupCLientName(String name) {
        return String.format("%s%s%s", MessageTypes.NAME_EXCHANGE, divider, name);
    }

    public static boolean isNameExchange(String messageBody) {
        String[] messageParts = messageBody.split(divider);
        MessageTypes messageType = MessageTypes.valueOf(messageParts[0]);
        return messageType.equals(MessageTypes.NAME_EXCHANGE);
    }

    public static boolean isMessageTo(String messageBody) {
        String[] messageParts = messageBody.split(divider);
        MessageTypes messageType = MessageTypes.valueOf(messageParts[0]);
        return messageType.equals(MessageTypes.MESSAGE);
    }

    public static String getValue(String messageBody) {
        String[] messageParts = messageBody.split(divider);
        return messageParts[1];
    }

    public static MessageData getMessageAndAddressee(String messageBody) {
        String[] messageParts = messageBody.split(divider);
        return new MessageData(Long.valueOf(messageParts[1]), Long.valueOf(messageParts[2]), messageParts[3]);
    }
}
