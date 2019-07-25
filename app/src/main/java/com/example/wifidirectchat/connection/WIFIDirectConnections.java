package com.example.wifidirectchat.connection;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;

import java.util.List;

public class WIFIDirectConnections {
    private List<WifiP2pDevice> deviceList;

    public void updateDeviceList(WifiP2pDeviceList list) {
        if (list.getDeviceList().equals(deviceList)) return;
        deviceList.clear();
        deviceList.addAll(list.getDeviceList());
    }

    public WifiP2pDevice getDevice(int ind) {
        if (deviceList == null) return null;
        if (ind < 0 || ind >= deviceList.size()) return null;
        return deviceList.get(ind);
    }
}