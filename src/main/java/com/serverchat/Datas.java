package com.serverchat;

import java.util.ArrayList;

import com.serverchat.types.ChatInterface;
import com.serverchat.types.User;

public class Datas {
    public ArrayList<User> allUsers;
    public ArrayList<ChatInterface> chatsData;
    
    public Datas() {
        allUsers = new ArrayList<>();
        chatsData = new ArrayList<>();
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

    //return an ArrayList of ChatInterface by UserID
    public ArrayList<ChatInterface> getChatsById(int id){
        ArrayList<ChatInterface> ans = new ArrayList<>();
        for(ChatInterface i : chatsData){
            if(i.getUsersId().contains(id)){
                ans.add(i);
            }
        }
        return ans;
    }

    //add di chat e gruppi
    public void addChatGroup(ChatInterface toAdd){
        chatsData.add(toAdd);
    }

    public boolean isExitingName(String name){
        for(User i : allUsers){
            if(i.getUsername().equals(name)) return true;
        }
        return false;
    }
}
