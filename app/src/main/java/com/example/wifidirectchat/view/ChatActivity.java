package com.example.wifidirectchat.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.example.wifidirectchat.R;
import com.example.wifidirectchat.model.MessageEntity;
import com.example.wifidirectchat.viewmodel.ChatPageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private View chatBox;
    private EditText newMessage;
    private Button sendMessage;
    private RecyclerView messages;
    private MessageListAdapter adapter;
    private String addressee;
    private String startDate;
    private boolean isOffline;
    private ChatPageViewModel model;
    private ConstraintLayout loadingScreen;
    private ConstraintLayout messengerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initChatPage();
        setupToolbar();

        if (isOffline) {
            chatBox.setVisibility(View.GONE);
            model.setAddressee(addressee);
        } else {
            loadingScreen.setVisibility(View.VISIBLE);
            messengerLayout.setVisibility(View.GONE);
            Objects.requireNonNull(getSupportActionBar()).hide();

            model.startSearch();
            model.chatIsReady().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
                        loadingScreen.setVisibility(View.GONE);
                        messengerLayout.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(getSupportActionBar()).show();
                    }
                }
            });
        }
        model.getMessageList().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(@Nullable List<MessageEntity> messageEntities) {
                Toast.makeText(ChatActivity.this,"update messages - " + messageEntities.size(),Toast.LENGTH_LONG).show();
                adapter.updateData(messageEntities);
            }
        });
    }

    private void initChatPage() {
        messengerLayout = findViewById(R.id.messengerLayout);
        chatBox = findViewById(R.id.layout_chatbox);
        loadingScreen = findViewById(R.id.loading_screen);
        loadingScreen.setVisibility(View.GONE);
        findViewById(R.id.stopSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.stopSearch();
                finish();
            }
        });
        isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE, false);
        addressee = getIntent().getStringExtra(Constants.ADDRESAT_NAME);

        model = ViewModelProviders.of(this).get(ChatPageViewModel.class);
        startDate = getIntent().getStringExtra(Constants.DATE);
        newMessage = findViewById(R.id.edittext_chatbox);
        sendMessage = findViewById(R.id.button_chatbox_send);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.sendMessage(newMessage.getText().toString());
            }
        });

        messages = findViewById(R.id.reyclerview_message_list);
        messages.setLayoutManager(new LinearLayoutManager(this, 1, true));
        adapter = new MessageListAdapter(new ArrayList<MessageEntity>());
        messages.setAdapter(adapter);
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle(addressee);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(startDate);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isOffline)
            return false;
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.delete_button) {
            model.deleteChat();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
