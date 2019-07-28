package com.example.wifidirectchat.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.wifidirectchat.model.ChatHistoryEntity;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> searchStatus;
    private MutableLiveData<List<ChatHistoryEntity>> history;

    public MainViewModel(@NonNull Application application) {
        super(application);
        searchStatus = new MutableLiveData<>();
        history = new MutableLiveData<>();
        List<ChatHistoryEntity> l = new ArrayList<>();
        //todo add database implementation
        history.setValue(l);
    }


    public void startSearch() {

    }

    public void stopSearch() {

    }

    public MutableLiveData<Boolean> getSearchStatus() {
        return searchStatus;
    }

    public MutableLiveData<List<ChatHistoryEntity>> getHistory() {
        return history;
    }

    public void clearHistory() {
    }
}
