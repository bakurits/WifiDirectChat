package com.example.wifidirectchat.connection;

import android.app.Service;

import com.example.wifidirectchat.db.MessageRepository;
import com.example.wifidirectchat.model.MessageEntity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

public class Server extends IMessenger {

    private Socket socket;
    private ServerSocket serverSocket;
    private String peerName;

    public Server(String peerName) {
        this.peerName = peerName;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(final String text) {

        new Thread() {
            @Override
            public void run() {
                if (socket == null) return;
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(text);
                    outputStream.flush();
                    Date c = Calendar.getInstance().getTime();
                    MessageEntity message = new MessageEntity(text, c, peerName, true);
                    MessageRepository.getInstance().insert(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void destroy(String text) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
