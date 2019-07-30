package com.example.wifidirectchat.connection;

import com.example.wifidirectchat.model.Message;
import com.example.wifidirectchat.viewmodel.ChatPageViewModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Messenger extends Thread {

    private Socket socket;

    private ChatPageViewModel.MessageHandler handler;

    public Messenger(Socket socket, ChatPageViewModel.MessageHandler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        while (socket != null) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                String message = (String) inputStream.readObject();
                if (message != null) {
                    handler.handleMessage(message, false);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(final String message) {
        new Thread() {
            @Override
            public void run() {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(message);
                    outputStream.flush();
                    handler.handleMessage(message, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
