package com.example.wifidirectchat.model;

public class ChatHistoryEntity {
    private String startDate;
    private String name;
    private int messageCount;

    public ChatHistoryEntity(String name, String startDate, int messageCount) {
        this.startDate = startDate;
        this.name = name;
        this.messageCount = messageCount;
    }


    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }
}
