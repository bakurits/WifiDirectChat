package com.example.wifidirectchat.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "messages")
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "content")
    private String message;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "addressee")
    private String addressee;

    @ColumnInfo(name = "sent-by-me")
    private boolean sentByMe;

    public MessageEntity(String message, Date date, String addressee, boolean sentByMe) {
        this.message = message;
        this.date = date;
        this.addressee = addressee;
        this.sentByMe = sentByMe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }

    public boolean isSentByMe() {
        return sentByMe;
    }

}
