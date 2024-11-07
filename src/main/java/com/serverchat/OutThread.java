package com.serverchat;

import java.io.DataOutputStream;
import java.io.IOException;

public class OutThread {
    private DataOutputStream out;

    public OutThread(DataOutputStream output){
        this.out = output;
    }

    //this function allow everyone who has
    //a working instance of OutThread to send data in the socket
        public boolean sendData(String data) {
        try {
            this.out.writeBytes(data + System.lineSeparator());
            return true;
        } catch (IOException e) {
            return false;    
        }
    } 
}
