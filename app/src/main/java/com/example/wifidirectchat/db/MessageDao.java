package com.example.wifidirectchat.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;


import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages")
    List<Message> getMatchedNotes();

    @Update
    void update(Message note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message dataModelEntity);

    @Delete
    void delete(Message user);

}
