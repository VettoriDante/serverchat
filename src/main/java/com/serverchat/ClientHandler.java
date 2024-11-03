package com.serverchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import com.google.gson.*;
import com.serverchat.protocol.CommandType;
import com.serverchat.protocol.JsonUser;
import com.serverchat.types.Chat;
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
        try {
            //try to use the socket
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
                        User newUser = new Gson().fromJson(inputs, User.class);//transform datas recived into a User
                        if(newUser == null){
                            out.writeBytes(CommandType.ERR_WRONG_DATA.toString() + "\n");//send error
                        }
                        else{
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
                            out.writeBytes(CommandType.ERR_NOT_FOUND.toString() + "\n");
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
            


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
