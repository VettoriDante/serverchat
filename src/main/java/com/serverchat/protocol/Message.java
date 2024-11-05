package com.serverchat.protocol;

/* The struct of a single message
 * by using this the server can insert the
 * message in the right chat and give all needed 
 * permission to right users 
 */
public class Message {
    private int id; //the server must set the message id (relative to the chat) => send it to the client
    private int chatId;
    private int senderId;
    private String senderName;
    private String content;
}
