package com.serverchat.types;

import java.util.ArrayList;

import com.serverchat.protocol.Message;

public interface ChatInterface {
    public ArrayList<Integer> getUsersId();
    public int getChatId();
    public String getChatName();
    public ArrayList<Message> getAllMessages();
}
