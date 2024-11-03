package com.serverchat.types;

import java.util.ArrayList;

public class Chat implements ChatInterface{
    private int id;
    private User user1;
    private User user2;
    private ArrayList<String> messages;

    public Chat(User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }

    //implements ChatInterface
    //Create a unique string with all the messagge of the chat
    @Override
    public String chatToString() {
        String ans = user1.getUsername() + " - ";
        ans += user2.getUsername();
        for(String i : messages){
            ans += i + "\n";
        }
        return ans;
    }

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
}
