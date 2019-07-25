package com.example.wifidirectchat.view;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wifidirectchat.Constants;
import com.example.wifidirectchat.model.Message;
import com.example.wifidirectchat.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private View chatBox;
    private EditText newMessage;
    private Button sendMessage;
    private RecyclerView messages;
    private MessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chatBox = findViewById(R.id.layout_chatbox);
        newMessage = findViewById(R.id.edittext_chatbox);
        sendMessage = findViewById(R.id.button_chatbox_send);
        messages = findViewById(R.id.reyclerview_message_list);
        messages.setLayoutManager(new LinearLayoutManager(this,1,true));
        boolean isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE,false);
        if(isOffline)
            chatBox.setVisibility(View.GONE);
        //////////////////////
        List<Message> m = new ArrayList<>();
        m.add(new Message("bakur yle xar","xutshabati","bakura", true));
        m.add(new Message("bakur magari yle xar","xutshabati","bakura", true));
        m.add(new Message("shenc magari yle xar","xutshabati","bakura", false));
        m.add(new Message("bakur yleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee xar","xutshabati","bakura", true));

        adapter = new MessageListAdapter(m);
        messages.setAdapter(adapter);


    }

}
