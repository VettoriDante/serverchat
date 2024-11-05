package com.serverchat.protocol;

import java.util.ArrayList;

public class ChatToSend {
    private int id;
    private String chatName;
    private ArrayList<Message> messages;

    public ChatToSend(int id, String chatName, ArrayList<Message> messages) {
        this.id = id;
        this.chatName = chatName;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    

    
}
