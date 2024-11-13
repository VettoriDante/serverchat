package com.serverchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import com.google.gson.*;
import com.serverchat.protocol.JsonChat;
import com.serverchat.protocol.JsonGroup;
import com.serverchat.protocol.CommandType;
import com.serverchat.protocol.JsonUser;
import com.serverchat.protocol.Message;
import com.serverchat.types.Chat;
import com.serverchat.types.ChatInterface;
import com.serverchat.types.Group;
import com.serverchat.types.User;

public class ClientHandler extends Thread {
    private User user;
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
                    case SEND_MSG:
                    //sending a new message
                    Message m = new Gson().fromJson(in.readLine(), Message.class);//try to cast to message
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
                    case NAV_CHAT:
                        String chatIdentifier = new Gson().fromJson(in.readLine(), String.class);
                        try{
                            int chatId;
                            chatId = Integer.parseInt(chatIdentifier);
                            if(datas.isAllownToChat(chatId, this.getUserId())){
                                WriteBytes(CommandType.OK);
                            }
                            else{
                                WriteBytes(CommandType.ERR_NOT_FOUND);
                            }
                        }catch(Exception e){
                            WriteBytes(CommandType.ERR_WRONG_DATA);
                        }
                    break;
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
                        case NEW_GROUP:
                        // get info to create new group 
                            JsonGroup newGroup = new Gson().fromJson(in.readLine(), JsonGroup.class); // try to cast check the group status
                            Group g = null;// create new group 
                            if(newGroup == null){
                                WriteBytes(CommandType.ERR_WRONG_DATA);
                            }
                            else{
                                // create a new group and then add all users
                                for(int i = 0; i < newGroup.getUsernameList().size(); i++){
                                    if(i == 0){
                                        // the first time it create the group and add first admin
                                        g = new Group(newGroup.getGroupName(), datas.getUserByName(newGroup.getUsernameList().get(0)));
                                    }
                                    else{
                                        //add every user
                                        g.addUser(datas.getUserByName(newGroup.getUsernameList().get(i)));
                                    }
                                }
                            }
                            datas.addChatGroup(g); // add group to datas 
                            WriteBytes(CommandType.OK); // send datas to client via WriteBytes 
                            
                            this.out.writeBytes(null);
                            break;
                        case REQ_CHATS:
                            in.readLine();//will recive null (no data required)
                            ArrayList<ChatInterface> allUserChats = datas.getChatsByUserId(this.getUserId());
                            ArrayList<JsonChat> chatsToSend = new ArrayList<>();
                            for(ChatInterface chat : allUserChats){
                                //create an array of JsonChat
                                chatsToSend.add(new JsonChat(chat.getChatId(), chat.getChatName(), chat.getAllMessages()));
                            }
                            WriteBytes(CommandType.OK);//send the OK
                            WriteBytes(new Gson().toJson(allUserChats));// send an array of JsonChat
                        case UPD_NAME:
                            JsonUser newUsername = new Gson().fromJson(in.readLine(), JsonUser.class);//read the new username
                            if(newUsername == null ){
                                WriteBytes(CommandType.ERR_WRONG_DATA);
                            } 
                            else
                            {
                                //set the username
                                this.user.setUsername(newUsername.getUsername());
                                WriteBytes(CommandType.OK);
                                this.out.writeBytes(null);
                            }
                        break;
                        case LOGOUT:
                            this.user = null;
                            this.Authentication();
                        case EXIT://close the socket
                            socket.close();//close the socket on clientLogout
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
