package com.example.wifidirectchat.connection;

import android.arch.lifecycle.MutableLiveData;

import com.example.wifidirectchat.LocalDevice;
import com.example.wifidirectchat.db.MessageRepository;
import com.example.wifidirectchat.model.MessageEntity;
import com.example.wifidirectchat.viewmodel.ChatPageViewModel;

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
    private ChatPageViewModel model;
    private MutableLiveData<Boolean> isConnected;
    private ObjectInputStream inputStream;

    public Client(String host, ChatPageViewModel model, MutableLiveData<Boolean> isConnected) {
        this.host = host;
        this.isConnected = isConnected;
        this.model = model;
    }

    @Override
    public void run() {
        this.socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, 8888), 5000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // დაკავშირების შემდეგ პირველ მესიჯად ვაგზავნით ჩვენი დივაისის სახელს
        send(LocalDevice.getInstance().getDevice().deviceName, false);

        // წაკითხულია თუ არა peer ის სახელი უკვე
        boolean isAddresseeSet = false;

        while (socket != null) {
            try {
                inputStream = new ObjectInputStream(socket.getInputStream());
                String messageText = (String) inputStream.readObject();
                if (messageText != null) {
                    if (isAddresseeSet) {
                        // თუ ახალი მესიჯი მოვიდა ვინახავთ მას პირდაპირ ბაზაში
                        // სწორედ ამ ბაზის ობიექტს აობსერვებს აქტივიტი, შესაბამისად წაკითხული ობიექტის
                        // აქთივითისთვის გაგზავნა აღარ გვიწევს
                        Date c = Calendar.getInstance().getTime();
                        MessageEntity message = new MessageEntity(messageText, c, peerName, false);
                        MessageRepository.getInstance().insert(message);
                    } else {
                        // პირველ მესიჯად ვკითხულობთ peer ის სახელს და შემდეგ ვრთავთ ჩატს
                        isAddresseeSet = true;
                        peerName = messageText;
                        model.setAddressee(messageText);
                        isConnected.postValue(true);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // თუ სოკეტი მეორე მხრიდან დაიხურა ვხურავთ ჩატის ფანჯარას ჩვენც
                model.closeChat();
            }
        }
    }

    @Override
    public void send(final String text, final boolean isMessage) {
        new Thread() {
            @Override
            public void run() {
                if (socket == null) return;
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(text);
                    outputStream.flush();
                    if (isMessage) {
                        // თუ პირველი მესიჯი არაა, ანუ სახელს არ ვაგზავნით
                        // მაშინ ბაზაშიც უნდა შევინახოთ
                        Date c = Calendar.getInstance().getTime();
                        MessageEntity message = new MessageEntity(text, c, peerName, true);
                        MessageRepository.getInstance().insert(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void DestroySocket() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
