package com.serverchat.types;

import java.util.ArrayList;

import javax.swing.GroupLayout.Group;

public class User {
    private int id;
    private String username;
    private String password;
    private ArrayList<Chat> chats;
    private ArrayList<Group> groups;

    

    //constructor
    public User(int id, String username, String password, ArrayList<Chat> chats, ArrayList<Group> groups) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.chats = chats;
        this.groups = groups;
    }
    
    //getters
    public String getUsername() {
        return username;
    }



    public String getPassword() {
        return password;
    }



    public int getId() {
        return id;
    }

    
}
