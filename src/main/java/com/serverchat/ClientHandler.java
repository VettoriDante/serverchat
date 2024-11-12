package com.serverchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.serverchat.protocol.JsonChat;
import com.serverchat.protocol.CommandType;
import com.serverchat.protocol.JsonUser;
import com.serverchat.protocol.Message;
import com.serverchat.types.Chat;
import com.serverchat.types.ChatInterface;
import com.serverchat.types.User;

import javax.swing.GroupLayout.Group;

public class ClientHandler extends Thread {
    private User user;
    private ArrayList<Chat> userChats;
    private ArrayList<Group> userGroups;
    private BufferedReader in;
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
        System.out.println("new client");
        // try to use the socket
        try {

            // read user info to establish connection
            boolean error = true;
            do {
                /*
                 * the authentication method
                 * return true whenever a error
                 * is found during the auth process
                 */
                error = Authentication();
            } while (error);

            // Once you know who the user is, the server get ready to send him
            // all of his chats
            ArrayList<ChatInterface> chats = datas.getChatsByUserId(this.user.getId());
            WriteBytes(this.getJSONToSend(chats));

            // here the thread add himself to datas (connected)
            datas.addConnectedClient(user.getId(), this);

            boolean connectionUP = true;
            String inCommand = null;
            CommandType command = null;
            do {
                // read and cast the new command
                inCommand = in.readLine();
                command = CommandType.valueOf(inCommand); // cast like operation
                String input = null; 
// 
                switch (command) {
                    case NEW_CHAT:
                        input = new Gson().fromJson(in.readLine(), String.class);//input will be username
                        //will be sent only the username of the other "component" and use this.user to create the chat
                        User t = datas.getUserByName(input);
                        Chat c = null;
                        if(t == null){this.WriteBytes(CommandType.ERR_NOT_FOUND);}
                        else{
                            c = new Chat(this.user, t);
                            datas.addChatGroup(c);
                        }
                        this.WriteBytes(c.getChatName() + "#" + c.getChatId());//send chatName and ChatID
                        break;
                    case SEND_MSG:
                        //sending a new message
                        input = in.readLine();//recive message
                        Message m = new Gson().fromJson(input, Message.class);//try to cast to message
                        //send error or ok
                        if(m == null){
                            this.WriteBytes(CommandType.ERR_WRONG_DATA);//not able to cast
                        }
                        else{
                            if(datas.addNewMsg(m)){
                                WriteBytes(CommandType.OK);
                                WriteBytes(m.getId()+"");//send the messageIdBack (the castToString is not fun)
                            }
                            else
                            {
                                WriteBytes(CommandType.ERR_GEN);//something went wrong with the message
                            }
                        }
                        break;
                    default:
                        WriteBytes(CommandType.ERR_WRONG_DATA);
                        break;
                }
                // here will go every operation
            } while (connectionUP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // the method do de authentication and return a boolean error
    private Boolean Authentication() throws IOException {

        String typeOfUser = in.readLine();// get the old/new user
        CommandType c = CommandType.valueOf(typeOfUser);// cast like operation c = command used
        String inputs = null;
        Boolean error = false;

        inputs = in.readLine();// get data

        switch (c) {
            case NEW_USER:
                // for new user wait to be sent all user info
                JsonUser newUserJ = new Gson().fromJson(inputs, JsonUser.class);// transform datas recived into a User
                if (newUserJ == null) {
                    WriteBytes(CommandType.ERR_WRONG_DATA);
                    error = true;
                } else {
                    if (datas.isExitingName(newUserJ.getUsername())) {
                        WriteBytes(CommandType.ERR_USER_EXISTS);
                    }
                    User newUser = new User(newUserJ.getUsername(), newUserJ.getPassword());
                    datas.newUser(newUser);// add the new user to the general array of datas
                    user = newUser; // set this.user
                    WriteBytes(CommandType.OK); // send the ok
                    System.out.println("new user has been created username: " + user.getUsername());
                }
                break;
            case OLD_USER:
                JsonUser searchFor = new Gson().fromJson(inputs, JsonUser.class);
                User tmp = datas.getUser(searchFor.getUsername(), searchFor.getPassword());
                // check if the user was successfully found
                if (tmp == null) {
                    if (datas.isExitingName(searchFor.getUsername())) {
                        WriteBytes(CommandType.ERR_NOT_FOUND);
                    } else {
                        WriteBytes(CommandType.ERR_WRONG_DATA);
                    }
                    error = true;
                } else {
                    WriteBytes(CommandType.OK);
                    user = tmp;
                    System.out.println(user.getUsername() + " has connected again :)");
                }
                break;
                case EXIT:
                //what to do on exit
            default:
                WriteBytes(CommandType.ERR_WRONG_DATA);
                error = true;
                break;
        }
        return error; // return error
    }

    //method that return the out of this client to be sent data
    public DataOutputStream getOutputStream(){
        return this.out;
    }

    //return this.user.getId
    public int getUserId(){
        return this.user.getId();
    } 


    // method for send a string , with before the implementation of "\n"
    private void WriteBytes(String stringtosout) throws IOException {
        out.writeBytes(stringtosout + "\n");
    }

    private void WriteBytes(CommandType commandToSout) throws IOException {
        out.writeBytes(commandToSout.toString() + "\n");
    }

    // return an array the Gson of ChatToSend and require an array of ChatInterface
    private String getJSONToSend(ArrayList<ChatInterface> chats) {
        ArrayList<JsonChat> toJson = new ArrayList<>();
        for (ChatInterface i : chats) {
            toJson.add(new JsonChat(i.getChatId(), i.getChatName(), i.getAllMessages()));
        }

        return new Gson().toJson(toJson);
    }

}
