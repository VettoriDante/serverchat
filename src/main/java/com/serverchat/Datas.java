package com.serverchat;

import java.util.ArrayList;

public class Datas {
    private ArrayList<User> allUsers;
    private ArrayList<ChatInterface> chatDatas;
    
    public Datas() {
        allUsers = new ArrayList<>();
        chatDatas = new ArrayList<>();
    }
    
    //TODO: check datas
    //add a new user got Created in ClientHandler
    public void newUser(User newUser){
        allUsers.add(newUser);
    }

    //return a user (if it exists) by Username && Password
    public User getUser(String userName, String password){
        for(User i : allUsers){
            if(userName.equals(i.getUsername()) && password.equals(i.getPassword())){
                return i;
            }
        }
        return null;
    }
}
