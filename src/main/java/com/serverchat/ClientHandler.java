package com.serverchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import com.google.gson.*;
import com.serverchat.protocol.ChatToSend;
import com.serverchat.protocol.CommandType;
import com.serverchat.protocol.JsonUser;
import com.serverchat.types.Chat;
import com.serverchat.types.ChatInterface;
import com.serverchat.types.User;

import javax.swing.GroupLayout.Group;

public class ClientHandler extends Thread {
    private User user;
    private ArrayList<Chat> userChats;
    private ArrayList<Group> userGroups;
    private Socket socket;
    private Datas datas;

    public ClientHandler(Socket socket, Datas datas) {
        this.socket = socket;
        this.datas = datas;
    }

    @Override
    public void run() {
        //try to use the socket
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //read user info to establish connection
            String typeOfUser = in.readLine();//get the old/new user
            CommandType c = CommandType.valueOf(typeOfUser);//cast like operation c = command used
            String inputs = null;

            inputs = in.readLine();//get data

            switch (c) {
                case NEW_USER:
                    //for new user wait to be sent all user info 
                        JsonUser newUserJ = new Gson().fromJson(inputs, JsonUser.class);//transform datas recived into a User
                        if(newUserJ == null){
                            out.writeBytes(CommandType.ERR_WRONG_DATA + "\n");//send error
                        }
                        else{
                            User newUser = new User(newUserJ.getUsername(), newUserJ.getPassword());
                            datas.newUser(newUser);//add the new user to the general array of datas 
                            user = newUser; // set this.user
                            out.writeBytes(CommandType.OK.toString() + "\n"); //send the ok 
                        }        
                    break;
                case OLD_USER:
                        JsonUser searchFor = new Gson().fromJson(inputs, JsonUser.class); 
                        User tmp = datas.getUser(searchFor.getUsername(), searchFor.getPassword());
                        //check if the user was successfully found
                        if(tmp == null){
                            out.writeBytes(CommandType.ERR_NOT_FOUND + "\n");
                        }
                        else{
                            out.writeBytes(CommandType.OK.toString() + "\n"); //send the ok 
                            user = tmp;
                        }
                    break;
                default:
                        out.writeBytes(CommandType.ERR_WRONG_DATA + "\n");
                    break;
            }

            //Once you know who the user is, the server get ready to send him
            //all of his chats
            ArrayList<ChatInterface> chats = datas.getChatsById(this.user.getId());    
            out.writeBytes( this.getJSONToSend(chats));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //return an array the Gson of ChatToSend and require an array of ChatInterface
    private String getJSONToSend(ArrayList<ChatInterface> chats){
        ArrayList<ChatToSend> toJson = new ArrayList<>();
        for(ChatInterface i : chats){
            toJson.add(new ChatToSend(i.getChatId(), i.getChatName(), i.getAllMessages()));
        }

        return new Gson().toJson(toJson);
    }
}
