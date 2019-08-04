package com.example.wifidirectchat.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.wifidirectchat.Constants;
import com.example.wifidirectchat.WiFiDirectBroadcastReceiver;
import com.example.wifidirectchat.connection.MessengerService;
import com.example.wifidirectchat.connection.WIFIDirectConnections;
import com.example.wifidirectchat.db.MessageRepository;
import com.example.wifidirectchat.model.MessageEntity;

import java.net.InetAddress;
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
    private Messenger mService = null;
    private boolean bound = false;
    private String addressee;


    private MutableLiveData<Boolean> chatIsReady;
    private LiveData<List<MessageEntity>> messageList;
    private MessageRepository repository;
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
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
        messageList = repository.getAllMessages(addressee);
    }

    public MutableLiveData<Boolean> chatIsReady() {
        return chatIsReady;
    }

    public LiveData<List<MessageEntity>> getMessageList() {
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
            //Toast.makeText(app, peers.toString(), Toast.LENGTH_LONG).show();

            if (connections != null) {
                if (!connections.updateDeviceList(peers)) return;
                if (connections.getDeviceCount() > 0) {

                    final WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = connections.getDevice(0).deviceAddress;
                    wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            setAddressee(connections.getDevice(0).deviceName);
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

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            bound = false;
        }
    };

    public void sendMessage(String text) {
        if (!bound) return;

        Message msg = Message.obtain(null, 1, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MSG_IN_BUNDLE, text);
        msg.setData(bundle);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deleteChat() {
        repository.deleteAllFrom(addressee);
    }


    public interface MessageHandler {
        void handleMessage(String message, boolean sendByMe);
    }

    public class Client extends Thread {
        String host;

        public Client(String host) {
            this.host = host;
        }

        @Override
        public void run() {
            Intent startIntent = new Intent(app.getApplicationContext(), MessengerService.class);
            startIntent.putExtra(Constants.IS_CLIENT, true);
            startIntent.putExtra(Constants.HOST_NAME, host);
            app.startService(startIntent);
        }
    }

    public class Server extends Thread {

        @Override
        public void run() {
            Intent startIntent = new Intent(app.getApplicationContext(), MessengerService.class);
            startIntent.putExtra(Constants.IS_CLIENT, false);
            app.startService(startIntent);
        }
    }

}
