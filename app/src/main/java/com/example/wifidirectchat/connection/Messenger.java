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
    private ObjectInputStream inputStream;
    private ObjectOutput outputStream;
    private ChatPageViewModel.MessageHandler handler;

    public Messenger(Socket socket, ChatPageViewModel.MessageHandler handler) {
        this.socket = socket;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.handler = handler;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();

        while (socket != null) {
            try {
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    handler.handleMessage(message);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(Message message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
