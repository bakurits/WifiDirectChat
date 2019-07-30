package com.example.wifidirectchat.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

import com.example.wifidirectchat.WiFiDirectBroadcastReceiver;
import com.example.wifidirectchat.connection.Messenger;
import com.example.wifidirectchat.connection.WIFIDirectConnections;
import com.example.wifidirectchat.model.Message;
import com.example.wifidirectchat.view.MainActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatPageViewModel extends AndroidViewModel {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Application app;
    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private WIFIDirectConnections connections;
    private Server server;
    private Client client;
    private Messenger messenger;

    private MutableLiveData<Boolean> chatIsReady;
    private MutableLiveData<List<Message>> messageList;
    private List<Message> messages;
    private boolean isConnected = false;

    public ChatPageViewModel(@NonNull Application application) {
        super(application);
        app = application;
        wifiP2pManager = (WifiP2pManager) app.getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(app.getApplicationContext(), app.getMainLooper(), null);
        broadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, peerListListener, connectionInfoListener);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        connections = new WIFIDirectConnections();
        registerReceiver();
        messages = new ArrayList<>();
        chatIsReady = new MutableLiveData<>();
        messageList = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> chatIsReady() {
        return chatIsReady;
    }

    public MutableLiveData<List<Message>> getMessageList() {
        return messageList;
    }


    public void registerReceiver() {
        app.getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
    }


    public void unregisterBroadcast() {
        app.getApplicationContext().unregisterReceiver(broadcastReceiver);
    }

    public void startSearch() {
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("", "success peer discovery");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("", "fail peer discovery");
            }
        });
    }

    public void stopSearch() {

    }


    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Log.e("new peer", peers.toString());
            if (connections != null) {
                if (!connections.updateDeviceList(peers)) return;
                if (connections.getDeviceCount() > 0) {
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = connections.getDevice(0).deviceAddress;
                    wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("", "connection success");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d("", "connection fail");
                        }
                    });
                }
            }
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            if (!info.groupFormed) return;
            if (isConnected) return;
            isConnected = true;
            Log.d("new connection", info.toString());
            final InetAddress address = info.groupOwnerAddress;

            if (info.isGroupOwner) {
                chatIsReady.setValue(true);
                server = new Server();
                server.start();
            } else {
                chatIsReady.setValue(true);
                Log.d("client is asdasd", "");
                client = new Client(address.getHostAddress());
                client.start();
            }
        }
    };

    private MessageHandler messageHandler = new MessageHandler() {
        @Override
        public void handleMessage(String messageText, boolean sendByMe) {
            Date c = Calendar.getInstance().getTime();
            Message message = new Message(messageText, c, "bejana", sendByMe);
            messages.add(message);
            messageList.postValue(messages);
        }
    };

    public void sendMessage(String text) {
        messenger.write(text);
    }

    public void deleteChat() {

    }


    public interface MessageHandler {
        void handleMessage(String message, boolean sendByMe);
    }

    public class Client extends Thread {
        String host;
        Socket socket;

        public Client(String host) {
            this.socket = new Socket();
            this.host = host;
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(host, 8888), 5000);
                messenger = new Messenger(socket, messageHandler);
                messenger.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Server extends Thread {

        private Socket socket;
        private ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                messenger = new Messenger( socket, messageHandler);
                messenger.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
