package com.example.wifidirectchat.connection;

import com.example.wifidirectchat.db.MessageRepository;
import com.example.wifidirectchat.model.MessageEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

public class Client extends IMessenger {
    private Socket socket;
    private String peerName;
    private String host;

    public Client(String host, String peerName) {
        this.peerName = peerName;
        this.host = host;
    }

    @Override
    public void run() {
        this.socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, 8888), 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (socket != null) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                String messageText = (String) inputStream.readObject();
                if (messageText != null) {
                    Date c = Calendar.getInstance().getTime();
                    MessageEntity message = new MessageEntity(messageText, c, peerName, false);
                    MessageRepository.getInstance().insert(message);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }
}
