package com.example.wifidirectchat.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.example.wifidirectchat.model.MessageEntity;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages")
    LiveData<List<MessageEntity>> getAllMessages();

    @Update
    void update(MessageEntity note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity dataModelEntity);

    @Delete
    void delete(MessageEntity user);

}
