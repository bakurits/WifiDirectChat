package com.example.wifidirectchat.connection;

import android.arch.lifecycle.MutableLiveData;

public abstract class IMessenger extends Thread {
    public abstract void send(String text, boolean isMessage);

    public abstract void DestroySocket();
}
