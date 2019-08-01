package com.example.wifidirectchat.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.example.wifidirectchat.model.MessageEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages WHERE addressee=:addressee ORDER BY date DESC")
    LiveData<List<MessageEntity>> getAllMessages(String addressee);

    @Update
    void update(MessageEntity note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity dataModelEntity);

    @Delete
    void delete(MessageEntity user);

    @Query("DELETE FROM messages WHERE addressee=:addressee")
    void deleteAllFrom(String addressee);

    @Query("DELETE FROM messages")
    void deleteAll();

    @Query("SELECT date FROM messages WHERE addressee = :addressee ORDER BY date ASC LIMIT 1")
    Date getStartDate(String addressee);

    @Query("SELECT COUNT(id) FROM messages WHERE addressee =:addressee")
    int getMessageCountFor(String addressee);

}
