package com.example.wifidirectchat.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.wifidirectchat.App;
import com.example.wifidirectchat.converters.DateConverter;


@Database(entities = {Message.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class MessagesDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "app_database";

    private static MessagesDatabase INSTANCE;

    private static final Object lock = new Object();

    public abstract MessageDao noteDao();

    static MessagesDatabase getInstance() {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                        App.getContext(),
                        MessagesDatabase.class,
                        DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }

}
