package com.serverchat.types;

import java.util.ArrayList;

import javax.swing.GroupLayout.Group;

public class User {
    private static int nextId = 0;
    private int id;
    private String username;
    private String password;
    private ArrayList<Chat> chats;
    private ArrayList<Group> groups;

    

    //constructor
    public User(String username, String password) {
        this.id = nextId++;
        this.username = username;
        this.password = password;
        this.chats = new ArrayList<>();
        this.groups = new ArrayList<>();
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
