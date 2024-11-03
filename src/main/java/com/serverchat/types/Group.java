package com.serverchat.types;

import java.util.ArrayList;

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
    private int id;
    private String groupName;
    private ArrayList<GroupUser> members;
    private ArrayList<String> messages;

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
    public String chatToString() {
        String ans = this.groupName + "\n";
        for(String i : this.messages){
            ans += i;
        }
        return ans;
    }
}
