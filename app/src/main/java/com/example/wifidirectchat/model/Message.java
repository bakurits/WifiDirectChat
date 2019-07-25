package com.example.wifidirectchat.model;

public class Message {
    private String message;
    private String date;
    private String addresat;
    private boolean sentByMe;
    public Message(String message, String date, String addresat, boolean sentByMe){
        this.message = message;
        this.date = date;
        this.addresat = addresat;
        this.sentByMe = sentByMe;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setAddresat(String addresat) {
        this.addresat = addresat;
    }

    public String getAddresat() {
        return addresat;
    }

    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }


    public boolean isSentByMe() {
        return sentByMe;
    }
}
