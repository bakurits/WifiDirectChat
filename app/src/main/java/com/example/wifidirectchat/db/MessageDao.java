package com.example.wifidirectchat.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.example.wifidirectchat.model.ChatHistoryEntity;
import com.example.wifidirectchat.model.MessageEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface MessageDao {
    static final String ALL_CHATS_QUERY = "SELECT\n" +
            "  users.username, latest_orders.created_at\n" +
            "FROM\n" +
            "  (SELECT\n" +
            "     user_id, MAX(created_at) AS created_at\n" +
            "   FROM\n" +
            "     orders\n" +
            "   GROUP BY\n" +
            "     user_id) AS latest_orders\n" +
            "INNER JOIN\n" +
            "  users\n" +
            "ON\n" +
            "  users.id = latest_orders.user_id";

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


    @Query("SELECT messages.id AS id, messages.addressee AS name, count(messages.id) AS messageCount , start_date_table.startDate AS date FROM messages INNER JOIN" +
            "(SELECT  messages.addressee AS addressee, MIN(messages.date) AS startDate FROM messages GROUP BY messages.addressee) AS start_date_table " +
            "ON messages.addressee = start_date_table.addressee " +
            "GROUP BY messages.addressee")
    LiveData<List<ChatHistoryEntity>> getAllChats();


}
