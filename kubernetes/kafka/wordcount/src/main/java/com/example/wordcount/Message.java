package com.example.wordcount;

public class Message {
    public String recipient;

    public String message;

    public Message() {
        // do nothing
    }

    public Message(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "recipient='" + recipient + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
