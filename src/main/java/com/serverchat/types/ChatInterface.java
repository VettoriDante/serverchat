package com.serverchat.types;

import java.util.ArrayList;

public interface ChatInterface {
    public ArrayList<Integer> getUsersId();
    public int getChatId();
    public String chatToString();
}
