package com.serverchat;

import java.io.DataOutputStream;
import java.util.ArrayList;

import com.serverchat.protocol.CommandType;
import com.serverchat.protocol.JsonChat;
import com.serverchat.protocol.JsonUser;
import com.serverchat.protocol.Message;
import com.serverchat.types.Chat;
import com.serverchat.types.ChatInterface;
import com.serverchat.types.User;
import com.google.gson.*;

public class Datas {
    public ArrayList<User> allUsers;
    public ArrayList<ChatInterface> chatsData;
    public ArrayList<ClientHandler> connectedUsers;
    
    public Datas() {
        allUsers = new ArrayList<>();
        chatsData = new ArrayList<>();
        connectedUsers = new ArrayList<>();//here we find userID/clientHandler, to know who must be sent new messages
        //theorically this should also allow the server to have more clients for the same user
    }
    
    //add a new user got Created in ClientHandler
    public synchronized void newUser(User newUser){
        allUsers.add(newUser);
    }

    //return the whole user by his username
    public User getUserByName(String username){
        for(User u : allUsers){
            if(u.getUsername().equals(username)) return u;
        }
        return null;
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
    public ArrayList<ChatInterface> getChatsByUserId(int id){
        ArrayList<ChatInterface> ans = new ArrayList<>();
        for(ChatInterface i : chatsData){
            if(i.getUsersId().contains(id)){
                ans.add(i);
            }
        }
        return ans;
    }

    public ChatInterface getChatByChatId(int chatId){
        for(ChatInterface i : chatsData){
            if(i.getChatId() == chatId)return i;
        }
        return null;
    }  

    //add of chat and groups
    public synchronized void addChatGroup(ChatInterface toAdd){
        chatsData.add(toAdd);
        for(ClientHandler i : this.connectedUsers){
            if(toAdd.getUsersId().contains(i.getUserId()) && i.getUserId() != toAdd.getUsersId().get(0)){
                //if the user is into the chat && is ConnectedNow
                sendNotifications(i, (toAdd instanceof Chat), toAdd);
            }
        }
    }

    //send notification every time a chat is created
    private synchronized void sendNotifications(ClientHandler client , boolean isChat , ChatInterface chat){
            //advise the user of the new incoming chat
            new OutThread (client.getOutputStream(), CommandType.NEW_CHAT.toString() ).start();
            JsonChat toSend = new JsonChat(chat.getChatId(), chat.getChatName(), chat.getAllMessages());
            new OutThread (client.getOutputStream(), new Gson().toJson(toSend)).start();//send data as a JsonChat OBJ
            //which is the "standard" to send and recive chats
    }

    //when a user try to create a username if it's already user return true
    public synchronized boolean isExitingName(String name){
        for(User i : allUsers){
            if(i.getUsername().equals(name)) return true;
        }
        return false;
    }

    // add an obj to connectedUser
    public synchronized void addConnectedClient(int userID, ClientHandler client){
        connectedUsers.add(client);
    }

    // get all user as JsonUser
    public synchronized ArrayList<JsonUser> getAllJsonUsers(){
        if(allUsers.size() == 0) return null;
        ArrayList<JsonUser> ans = new ArrayList<>();
        for(User i : allUsers){
            ans.add(new JsonUser(i.getUsername(), i.getPassword()));
        }
        return ans;
    }

    //send new message
    public boolean addNewMsg(Message message){
        //get the chat of this message
        ChatInterface c = this.getChatByChatId(message.getChatId());
        //add the message into the chat
        int val = c.addNewMsg(message);
        if(val < 0) return false;// if the user was not in the chat
        //check 
        sendMessageToOthers(message, c);
        return true;
    }

    //this search for each user in the client
    private void sendMessageToOthers(Message m, ChatInterface c){
        for(ClientHandler client : this.connectedUsers){//searching into connected user for every user of this chat
            if(c.getUsersId().contains(client.getUserId()) && client.getUserId() != m.getSenderId()){//check if the client is in the chat
                //and its different from the sender

                //found connected user now sent him data
                DataOutputStream out = client.getOutputStream();//get the outputStream of the client which will be sent data
                //passing the output OBJ
                new OutThread(out, new Gson().toJson(m)).start();
            }
        }
    }

    //get user allown to chat
    public boolean isAllownToChat(int chatId, int userID){
        if(this.getChatByChatId(chatId).getUsersId().contains(userID))return true;
        return false;
    }


}
