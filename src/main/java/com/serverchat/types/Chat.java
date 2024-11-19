package com.serverchat.types;

import java.util.ArrayList;

import com.serverchat.protocol.Message;

public class Chat implements ChatInterface{
    private int messagesID;
    private int id;
    private User user1;
    private User user2;
    private ArrayList<Message> messages;

    //constructor
    public Chat(User user1, User user2){
        this.messages = new ArrayList<>();
        this.id = ChatIDs.getNextChatID();
        this.user1 = user1;
        this.user2 = user2;
        this.messagesID = 0;
    }

    //implements ChatInterface

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

    //return a chatname generated by summing usernames
    @Override
    public String getChatName() {
        return user1.getUsername() + "-" + user2.getUsername();
    }

    //return the array of all messages
    @Override
    public ArrayList<Message> getAllMessages() {
        return this.messages;
    }

    @Override
    public int addNewMsg(Message message) {
        message.setId(messagesID++);
        for(Message m : messages){
            if(m.getId() == message.getId()) return -1;
        }
        messages.add(message);
        return message.getId();
    }

    //if return null there is no message with that ID
    //and if the user has sent that message
    private Message getMessageByID(int messageId, int userID){
        Message m = null;
        for(Message i : messages){
            if(i.getId() == messageId) m = i;
        }
        if(m == null) return null;
        if(m.getSenderId() != userID) return null;//double if to avoid errors
        return m ;
    } 

    @Override
    public boolean rmMessage(int messageId, int userID) {
        Message m = this.getMessageByID(messageId , userID);
        if(m == null) return false;
            messages.remove(m);
        return true;
    }

   
    @Override
    public boolean modMsg(Message message, int userID) {
        Message m = this.getMessageByID(message.getId(), userID);
        if(m == null) return false;
        
        m.setContent(message.getContent());
        return true;
    }

    @Override
    public void rmUser(User user, User deletedUserInfo) {
        if(user1.getId() == user.getId()){user1 = deletedUserInfo; return;}
        if(user2.getId() == user.getId()){user2 = deletedUserInfo; return;}
    }
}
