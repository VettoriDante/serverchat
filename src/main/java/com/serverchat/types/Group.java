package com.serverchat.types;

import java.util.ArrayList;

import com.serverchat.protocol.Message;

public class Group implements ChatInterface{
    //Intenal class used to give each user a "role"
    private class GroupUser{
        private boolean isAdmin;
        private User user;
        // private long joinedAt;

        public GroupUser(boolean isAdmin, User user) {
            this.isAdmin = isAdmin;
            this.user = user;
        }

        public User getUser(){
            return user;
        }
    }
    //basic var
    private static int nextId = 0;
    private int id;
    private String groupName;
    private ArrayList<GroupUser> members;
    private ArrayList<Message> messages;

    
    public Group(String name,User firstUser) {
        this.id = nextId++;
        this.members = new ArrayList<>();
        this.groupName = name;
        members.add(new GroupUser(true, firstUser));
    }

    //return the chat id
    @Override
    public int getChatId() {
        return id;
    }

    //return the list of all users
    @Override
    public ArrayList<Integer> getUsersId() {
        ArrayList<Integer> ans = new ArrayList<>();
        for(GroupUser i : members){
            ans.add(i.getUser().getId());
        }
        return ans;
    }

    @Override
    public String getChatName() {
        return this.getChatName();
    }

    @Override
    public ArrayList<Message> getAllMessages() {
        return this.messages;
    }
}
