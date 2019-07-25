package com.example.wifidirectchat.View;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifidirectchat.Model.ChatHistoryEntity;
import com.example.wifidirectchat.R;
import com.example.wifidirectchat.ViewModel.MainViewModel;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private ConstraintLayout loadingScreen;
    private Button clearHistoryButton;
    private TextView emptyPageMessage;

    private RecyclerView chatHistoryView;
    private ChatListAdapter historyAdapter;

    private MainViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpDrawer();
        setUpHistoryPage();
        setUpLoadingScreen();
        setUpViewModel();

    }

    private void setUpViewModel() {
        model = ViewModelProviders.of(this).get(MainViewModel.class);
        model.getSearchStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean status) {
                assert status != null;
                if(status.equals(true)){
                    //ToDo change activity
                    Toast.makeText(MainActivity.this,"new chat found",Toast.LENGTH_LONG).show();
                }
            }
        });

        model.getHistory().observe(this, new Observer<List<ChatHistoryEntity>>() {
            @Override
            public void onChanged(@Nullable List<ChatHistoryEntity> chats) {
                assert chats != null;

                Toast.makeText(MainActivity.this,"chat list changed",Toast.LENGTH_LONG).show();

                if(chats.size()==0){
                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.historyPageTitle));
                    emptyPageMessage.setVisibility(View.VISIBLE);
                    chatHistoryView.setVisibility(View.GONE);
                    clearHistoryButton.setVisibility(View.GONE);
                }else{
                    emptyPageMessage.setVisibility(View.GONE);
                    chatHistoryView.setVisibility(View.VISIBLE);
                    clearHistoryButton.setVisibility(View.VISIBLE);
                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.historyPageTitle)+"(" + chats.size() + ")");
                }
                historyAdapter.updateData(chats);
            }
        });
    }

    private void setUpLoadingScreen() {
        loadingScreen = findViewById(R.id.loading_screen);
        loadingScreen.setVisibility(View.GONE);
        Button cancelSearchButton = findViewById(R.id.stopSearch);
        cancelSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.stopSearch();
                drawer.setVisibility(View.VISIBLE);
                loadingScreen.setVisibility(View.GONE);
                Objects.requireNonNull(getSupportActionBar()).show();
            }
        });
    }

    private void setUpHistoryPage() {
        clearHistoryButton = findViewById(R.id.clearHistory);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.clearHistory();
            }
        });
        chatHistoryView = findViewById(R.id.chatHistory);
        chatHistoryView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new ChatListAdapter();
        chatHistoryView.setAdapter(historyAdapter);
        emptyPageMessage = findViewById(R.id.chat_list_empty_message);
    }


    private void setUpDrawer(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setVisibility(View.VISIBLE);
        toggle = new ActionBarDrawerToggle(this,drawer,R.string.Open,R.string.Close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.historyPageTitle));


        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu_chat_button){
                    Objects.requireNonNull(getSupportActionBar()).hide();
                    drawer.setVisibility(View.GONE);
                    loadingScreen.setVisibility(View.VISIBLE);
                    model.startSearch();
                }
                drawer.closeDrawers();
                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
