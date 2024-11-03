package com.serverchat;

import java.util.ArrayList;

import com.serverchat.types.ChatInterface;
import com.serverchat.types.User;

public class Datas {
    public ArrayList<User> allUsers;
    public ArrayList<ChatInterface> chatDatas;
    
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

    //return a user by his id
    public User getUserById(int id){
        for(User u : allUsers){
            if(u.getId() == id) return u;
        }
        return null;
    }
}
