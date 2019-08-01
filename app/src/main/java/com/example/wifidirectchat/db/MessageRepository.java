package com.example.wifidirectchat.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.wifidirectchat.model.Message;

import java.util.Date;
import java.util.List;

public class MessageRepository {
        private static MessageRepository INSTANCE  = new MessageRepository();
        private MessageDao messageDao;

        private static MessageRepository getInstance(){
            return INSTANCE;
        }

        private MessageRepository(){
            MessagesDatabase db = MessagesDatabase.getInstance();
            messageDao = db.messageDao();
        }

        public void update(Message message){
            new UpdateAsyncTask(messageDao).execute(message);
        }

        public void insert(Message message){
            new InsertAsyncTask(messageDao).execute(message);
        }

        public void delete(Message message){
            new DeleteAsyncTask(messageDao).execute(message);
        }

        public void deleteAllFrom(String addressee){
            new DeleteAllFromAsyncTask(messageDao).execute(addressee);
        }

        public void deleteAll(){
            new DeleteAllAsyncTask(messageDao).execute();
        }

        public void getStartDate(String addressee){

        }

        public void getMessageCountFor(String addressee){

        }

        public LiveData<List<Message>> getAllMessages(String addressee){
            return messageDao.getAllMessages(addressee);
        }



        private static class InsertAsyncTask extends AsyncTask<Message, Void, Void>{
            private MessageDao messageDao;
            private InsertAsyncTask(MessageDao dao){
                this.messageDao = dao;
            }
            @Override
            protected Void doInBackground(Message... messages) {
                messageDao.insert(messages[0]);
                return null;
            }
        }

        private static class UpdateAsyncTask extends AsyncTask<Message, Void, Void>{
            private MessageDao messageDao;
            private UpdateAsyncTask(MessageDao dao){
                this.messageDao = dao;
            }
            @Override
            protected Void doInBackground(Message... messages) {
                messageDao.update(messages[0]);
                return null;
            }
        }

        private static class DeleteAsyncTask extends AsyncTask<Message, Void, Void>{
            private MessageDao messageDao;
            private DeleteAsyncTask(MessageDao dao){
                this.messageDao = dao;
            }
            @Override
            protected Void doInBackground(Message... messages) {
                messageDao.delete(messages[0]);
                return null;
            }
        }
        private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void>{
            private MessageDao messageDao;
            private DeleteAllAsyncTask(MessageDao dao){
                this.messageDao = dao;
            }
            @Override
            protected Void doInBackground(Void... voids) {
                messageDao.deleteAll();
                return null;
            }
        }
        private static class DeleteAllFromAsyncTask extends AsyncTask<String, Void, Void>{
            private MessageDao messageDao;
            private DeleteAllFromAsyncTask(MessageDao dao){
                this.messageDao = dao;
            }
            @Override
            protected Void doInBackground(String... addressees) {
                messageDao.deleteAllFrom(addressees[0]);
                return null;
            }
        }

        private static class StartDateAsyncTask extends AsyncTask<String, Void, Void>{
            private MessageDao messageDao;
            private StartDateAsyncTask(MessageDao dao){
                this.messageDao = dao;
            }
            @Override
            protected Void doInBackground(String... addressees) {
                messageDao.getStartDate(addressees[0]);
                return null;
            }
        }
        private static class MessageCountAsyncTask extends AsyncTask<String, Void, Void>{
            private MessageDao messageDao;
            private MessageCountAsyncTask(MessageDao dao){
                this.messageDao = dao;
            }
            @Override
            protected Void doInBackground(String... addressees) {
                messageDao.getMessageCountFor(addressees[0]);
                return null;
            }
        }
}
