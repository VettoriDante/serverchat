package com.serverchat.types;

import java.util.ArrayList;

import com.serverchat.protocol.Message;

public class Group implements ChatInterface {
    // basic var
    private static int nextId = 0;
    private int messageID;
    private int id;
    private String groupName;
    private ArrayList<GroupUser> members;
    private ArrayList<Message> messages;

    // constructor with generated ID
    public Group(String name, User firstUser) {
        this.id = nextId++;
        this.members = new ArrayList<>();
        this.groupName = name;
        members.add(new GroupUser(true, firstUser));
    }

    // return the chat id
    @Override
    public int getChatId() {
        return id;
    }

    // return the list of all users
    @Override
    public ArrayList<Integer> getUsersId() {
        ArrayList<Integer> ans = new ArrayList<>();
        for (GroupUser i : members) {
            ans.add(i.getUser().getId());
        }
        return ans;
    }

    @Override
    public String getChatName() {
        return this.groupName;
    }

    @Override
    public ArrayList<Message> getAllMessages() {
        return this.messages;
    }

    // Intenal class used to give each user a "role"
    private class GroupUser {
        private boolean isAdmin;
        private User user;
        // private long joinedAt;

        public GroupUser(boolean isAdmin, User user) {
            this.isAdmin = isAdmin;
            this.user = user;
        }

        //return this user
        public User getUser() {
            return user;
        }

        //return the role of a user into this group
        public boolean isAdmin() {
            return isAdmin;
        }
        
        //allow to change the role of a user into the Group
        public void setAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
        }

        
    }

    @Override
    public int addNewMsg(Message message) {
        for(Message m : messages){
            if(m.getId() == message.getId()) return -1;
        }
       message.setId(message.getId() + 1) ;
       messages.add(message);
       return message.getId();
    }
   

}
