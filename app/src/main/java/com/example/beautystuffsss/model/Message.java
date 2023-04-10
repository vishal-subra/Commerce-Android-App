package com.example.beautystuffsss.model;

public class Message {
    int userType;
    String recipientId;
    String myId;
    String userName;
    String userMessage;
    String time;
    String date;

    public Message(int userType, String recipientId, String myId, String userName, String userMessage, String time, String date) {
        this.recipientId = recipientId;
        this.myId = myId;
        this.userName = userName;
        this.userMessage = userMessage;
        this.time = time;
        this.date = date;
        this.userType = userType;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getMyId() {
        return myId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
