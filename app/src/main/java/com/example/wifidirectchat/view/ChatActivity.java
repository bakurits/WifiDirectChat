package com.example.wifidirectchat.view;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private String adressat;
    private String startDate;
    private boolean isOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatBox = findViewById(R.id.layout_chatbox);
        isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE,false);
        if(isOffline)
            chatBox.setVisibility(View.GONE);
        adressat = getIntent().getStringExtra(Constants.ADDRESAT_NAME);
        startDate= getIntent().getStringExtra(Constants.DATE);
        setupToolbar();
        newMessage = findViewById(R.id.edittext_chatbox);
        sendMessage = findViewById(R.id.button_chatbox_send);
        messages = findViewById(R.id.reyclerview_message_list);
        messages.setLayoutManager(new LinearLayoutManager(this,1,true));
        Toast.makeText(this, adressat,Toast.LENGTH_SHORT).show();
        //////////////////////
        List<Message> m = new ArrayList<>();
        m.add(new Message("bakur yle xar","xutshabati","bakura", true));
        m.add(new Message("bakur magari yle xar","xutshabati","bakura", true));
        m.add(new Message("shenc magari yle xar","xutshabati","bakura", false));
        m.add(new Message("bakur yleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee xar","xutshabati","bakura", true));

        adapter = new MessageListAdapter(m);
        messages.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(adressat);
        getSupportActionBar().setSubtitle(startDate);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isOffline)
            return false;
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.delete_button) {
            Toast.makeText(this, "Action clicked", Toast.LENGTH_LONG).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
