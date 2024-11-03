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

            switch (c) {
                case NEW_USER:
                    //for new user wait to be sent all user info 
                        inputs = in.readLine();//get all newUser data
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
                        inputs = in.readLine();
                        JsonUser searchFor = new Gson().fromJson(inputs, JsonUser.class); 
                        datas.getUser(searchFor.getUsername(), searchFor.getPassword());
                    break;
                default:
                    break;
            }
            String userInfo = in.readLine();//get the user
            JsonUser userData = new Gson().fromJson(userInfo, JsonUser.class);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
