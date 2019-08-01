package com.example.wifidirectchat.connection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;


import com.example.wifidirectchat.Constants;
import com.example.wifidirectchat.viewmodel.ChatPageViewModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MessengerService extends Service {

    private Socket socket;
    private ServerSocket serverSocket;


    public MessengerService() {
    }

    private void start() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (socket != null) {
                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                        String message = (String) inputStream.readObject();
                        if (message != null) {

                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.run();

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        private Context applicationContext;
        private Socket socket;

        IncomingHandler(Context context, Socket socket) {
            applicationContext = context.getApplicationContext();
            this.socket = socket;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String res = b.getString(Constants.MSG_IN_BUNDLE, "");
            if (res.length() > 0)
                write(res);
        }


        private void write(final String message) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject(message);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    Messenger mMessenger;

    @Override
    public IBinder onBind(Intent intent) {
        boolean isClient = intent.getBooleanExtra(Constants.IS_CLIENT, false);
        if (isClient) {
            String host = intent.getStringExtra(Constants.HOST_NAME);
            this.socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host, 8888), 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMessenger = new Messenger(new IncomingHandler(this, socket));
        return mMessenger.getBinder();

    }
}
