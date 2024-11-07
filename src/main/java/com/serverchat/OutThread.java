package com.serverchat;

import java.io.DataOutputStream;
import java.io.IOException;

public class OutThread extends Thread{
    private DataOutputStream out;
    private String data;

    public OutThread(DataOutputStream output, String dataToSend){
        this.out = output;
        this.data = dataToSend;
    }

    //this function allow everyone who has
    //a working instance of OutThread to send data in the socket
        public void run() {
            try {
                this.out.writeBytes(data + System.lineSeparator());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    } 
}
