package com.example.wifidirectchat.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import android.support.annotation.NonNull;
import android.util.Log;


import com.example.wifidirectchat.WiFiDirectBroadcastReceiver;
import com.example.wifidirectchat.connection.Client;
import com.example.wifidirectchat.connection.IMessenger;
import com.example.wifidirectchat.connection.Server;
import com.example.wifidirectchat.connection.WIFIDirectConnections;
import com.example.wifidirectchat.db.MessageRepository;
import com.example.wifidirectchat.model.MessageEntity;

import java.net.InetAddress;

import java.util.List;

public class ChatPageViewModel extends AndroidViewModel {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Application app;
    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private WIFIDirectConnections connections;
    private IMessenger messenger;
    private String addressee;
    private MessageRepository repository;


    private MutableLiveData<Boolean> chatIsReady;
    private LiveData<List<MessageEntity>> messageList;
    private MutableLiveData<List<WifiP2pDevice>> peerList;
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
        repository = MessageRepository.getInstance();
        chatIsReady = new MutableLiveData<>();
        messageList = new MutableLiveData<>();
        peerList = new MutableLiveData<>();
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
        messageList = repository.getAllMessages(this.addressee);
    }

    public MutableLiveData<Boolean> chatIsReady() {
        return chatIsReady;
    }

    public LiveData<List<MessageEntity>> getMessageList() {
        return messageList;
    }

    public MutableLiveData<List<WifiP2pDevice>> getPeerList() {
        return peerList;
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

    public void connectToPeer(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
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

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Log.e("new peer", peers.toString());

            if (connections != null) {
                if (!connections.updateDeviceList(peers)) return;
                if (connections.getDeviceCount() > 0) {
                    peerList.postValue(connections.getDeviceList());
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
                Server server = new Server("bejana");
                server.start();
                messenger = server;
            } else {
                chatIsReady.setValue(true);
                Client client = new Client(address.getHostAddress(), "bejana");
                client.start();
                messenger = client;
            }
        }
    };


    public void sendMessage(String text) {
        messenger.send(text);
    }


    public void closeChat() {
    }

    public void deleteChat() {
        repository.deleteAllFrom(addressee);
    }

}
