package com.example.wifidirectchat.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.wifidirectchat.Constants;
import com.example.wifidirectchat.db.MessageRepository;
import com.example.wifidirectchat.model.ChatHistoryEntity;
import com.example.wifidirectchat.model.MessageEntity;
import com.example.wifidirectchat.view.ChatActivity;
import com.example.wifidirectchat.view.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> searchStatus;
    private LiveData<List<ChatHistoryEntity>> history;
    private MessageRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        searchStatus = new MutableLiveData<>();
        repository = MessageRepository.getInstance();
//        repository.insert(new MessageEntity("message 1",new Date(System.currentTimeMillis()),"bakura",true));
//        repository.insert(new MessageEntity("message 2",new Date(System.currentTimeMillis() + 100),"bakura",false));
//        repository.insert(new MessageEntity("message 3",new Date(System.currentTimeMillis() + 200),"bakura",false));
//        repository.insert(new MessageEntity("message 4",new Date(System.currentTimeMillis() + 300),"gegi",true));
//        repository.insert(new MessageEntity("message 5",new Date(System.currentTimeMillis() + 400),"gegi",false));
//        repository.insert(new MessageEntity("message 6",new Date(System.currentTimeMillis() + 500),"bejana",true));
//        repository.insert(new MessageEntity("message 7",new Date(System.currentTimeMillis() + 600),"bejana",true));
//        repository.insert(new MessageEntity("message 8",new Date(System.currentTimeMillis() + 700),"bejana",true));
//        repository.insert(new MessageEntity("message 9",new Date(System.currentTimeMillis() + 800),"bejana",true));


        history = repository.getAllChats();
    }


    public void startSearch() {
        Intent intent = new Intent(getApplication(), ChatActivity.class);
        intent.putExtra(Constants.IS_OFFLINE, false);
        getApplication().startActivity(intent);
    }


    public LiveData<List<ChatHistoryEntity>> getHistory() {
        return history;
    }

    public void clearHistory() {
        repository.deleteAll();
    }
}
