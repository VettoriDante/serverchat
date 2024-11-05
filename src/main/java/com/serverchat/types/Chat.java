package com.serverchat.types;

import java.util.ArrayList;

import com.serverchat.protocol.Message;

public class Chat implements ChatInterface{
    private static int nextId = 0;
    private int id;
    private User user1;
    private User user2;
    private ArrayList<Message> messages;

    public Chat(User user1, User user2){
        this.id = nextId++;
        this.user1 = user1;
        this.user2 = user2;
    }

    //implements ChatInterface

    //create and return an array with users id
    @Override
    public ArrayList<Integer> getUsersId() {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(user1.getId());
        ids.add(user2.getId());
        return ids;
    }

    //return the chat id
    @Override
    public int getChatId() {
        return this.id;
    }

    @Override
    public String getChatName() {
        return user1.getUsername() + " - " + user2.getUsername();
    }

    @Override
    public ArrayList<Message> getAllMessages() {
        return this.messages;
    }
}
