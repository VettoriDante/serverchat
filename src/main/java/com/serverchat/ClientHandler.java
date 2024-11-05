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
    private BufferedReader in ;
    private DataOutputStream out;
    private Socket socket;
    private Datas datas;

    public ClientHandler(Socket socket, Datas datas) {
        this.socket = socket;
        this.datas = datas;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //try to use the socket
        try {
           

            //read user info to establish connection
            boolean error = true;
            do{
              error =  Authentication();
            }while(error);

            //Once you know who the user is, the server get ready to send him
            //all of his chats
            ArrayList<ChatInterface> chats = datas.getChatsById(this.user.getId());    
            out.writeBytes( this.getJSONToSend(chats));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // method for send a string , with before the implementation of "\n"
    private void WriteBytes(String stringtosout) throws IOException{ 
        out.writeBytes(stringtosout + "\n");
    }
    
    //return an array the Gson of ChatToSend and require an array of ChatInterface
    private String getJSONToSend(ArrayList<ChatInterface> chats){
        ArrayList<ChatToSend> toJson = new ArrayList<>();
        for(ChatInterface i : chats){
            toJson.add(new ChatToSend(i.getChatId(), i.getChatName(), i.getAllMessages()));
        }

        return new Gson().toJson(toJson);
    }

    // the method do de authentication and return a boolean error 
    private Boolean Authentication() throws IOException  {

        String typeOfUser = in.readLine();// get the old/new user
        CommandType c = CommandType.valueOf(typeOfUser);// cast like operation c = command used
        String inputs = null;
        Boolean error = false ;

        inputs = in.readLine();// get data

        switch (c) {
            case NEW_USER:
                // for new user wait to be sent all user info
                JsonUser newUserJ = new Gson().fromJson(inputs, JsonUser.class);// transform datas recived into a User
                if (newUserJ == null) {
                    WriteBytes(CommandType.ERR_WRONG_DATA.toString());
                    error = true;
                } else {
                    if(datas.isExitingName(newUserJ.getUsername())){
                        WriteBytes(CommandType.ERR_USER_EXISTS.toString());
                    }
                    User newUser = new User(newUserJ.getUsername(), newUserJ.getPassword());
                    datas.newUser(newUser);// add the new user to the general array of datas
                    user = newUser; // set this.user
                   WriteBytes(CommandType.OK.toString()); // send the ok
                    
                }
                break;
            case OLD_USER:
                JsonUser searchFor = new Gson().fromJson(inputs, JsonUser.class);
                User tmp = datas.getUser(searchFor.getUsername(), searchFor.getPassword());
                // check if the user was successfully found
                if (tmp == null) {
                    WriteBytes(CommandType.ERR_NOT_FOUND.toString() );
                    error = true;
                } else {
                   WriteBytes(CommandType.OK.toString());
                    user = tmp;
                }
                break;
            default:
                WriteBytes(CommandType.ERR_WRONG_DATA.toString());
                error = true;
                break;
        }
        return error; // return error
    }
}
