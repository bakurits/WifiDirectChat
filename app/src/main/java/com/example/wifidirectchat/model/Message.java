package com.example.wifidirectchat.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "content")
    private String message;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "addresat")
    private String addresat;

    @ColumnInfo(name = "sent-by-me")
    private boolean sentByMe;

    public Message(String message, Date date, String addresat, boolean sentByMe) {
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

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
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
