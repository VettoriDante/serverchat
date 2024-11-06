package com.serverchat;

import java.util.ArrayList;

import com.serverchat.types.ChatInterface;
import com.serverchat.types.User;

public class Datas {
    private class UserClient {
        private int usedID;
        private ClientHandler client;
        
        public UserClient(int usedID, ClientHandler client) {
            this.usedID = usedID;
            this.client = client;
        }

        public int getUsedID() {
            return usedID;
        }

        public void setUsedID(int usedID) {
            this.usedID = usedID;
        }

        public ClientHandler getClient() {
            return client;
        }

        public void setClient(ClientHandler client) {
            this.client = client;
        }

        
    }
    public ArrayList<User> allUsers;
    public ArrayList<ChatInterface> chatsData;
    public ArrayList<UserClient> connectedUsers;
    
    public Datas() {
        allUsers = new ArrayList<>();
        chatsData = new ArrayList<>();
        connectedUsers = new ArrayList<>();
    }
    
    //TODO: check datas
    //add a new user got Created in ClientHandler
    public synchronized void newUser(User newUser){
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
    public synchronized void addChatGroup(ChatInterface toAdd){
        chatsData.add(toAdd);
    }

    //when a user try to create a username if it's already used return true
    public boolean isExitingName(String name){
        for(User i : allUsers){
            if(i.getUsername().equals(name)) return true;
        }
        return false;
    }

    public void addConnectedClient(int userID, ClientHandler client){
        connectedUsers.add(new UserClient(userID, client));
    }
}
