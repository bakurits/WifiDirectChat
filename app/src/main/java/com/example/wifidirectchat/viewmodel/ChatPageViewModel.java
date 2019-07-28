package com.example.wifidirectchat.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
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

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }


    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Log.d("new peer", peers.toString());
            if (connections != null)
                connections.updateDeviceList(peers);
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            if (!info.groupFormed) return;
            Log.d("new connection", info.toString());
            final InetAddress address = info.groupOwnerAddress;
            if (info.isGroupOwner) {
                server = new Server();
                server.start();
            } else {
                client = new Client(address.getHostAddress());
                client.start();
            }
        }
    };

    private MessageHandler messageHandler = new MessageHandler() {
        @Override
        public void handleMessage(Message message) {

        }
    };

    public interface MessageHandler {
        void handleMessage(Message message);
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
                socket.connect(new InetSocketAddress(host, 8888), 500);
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
                messenger = new Messenger(socket, messageHandler);
                messenger.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
