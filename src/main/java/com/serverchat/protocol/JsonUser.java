package com.serverchat.protocol;

/* Used in the authentication phase to 
 * recognise a user without all his info
 */
public class JsonUser {
    private String username;
    private String password;

    public JsonUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}