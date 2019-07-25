package com.example.wifidirectchat.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifidirectchat.model.ChatHistoryEntity;
import com.example.wifidirectchat.R;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {
    private List<ChatHistoryEntity> chats;
    private OnChatClickListener listener;


    ChatListAdapter() {
        chats = new ArrayList<>();
        listener = new OnChatClickListener() {
            @Override
            public void onChatClick(ChatHistoryEntity chat, Context context) {
                Toast.makeText(context, chat.getName(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_history_item_holder, viewGroup, false);
        return new ChatHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder chatHolder, int i) {
        ChatHistoryEntity chat = chats.get(i);
        chatHolder.messageCount.setText(String.valueOf(chat.getMessageCount()));
        chatHolder.startDate.setText(chat.getStartDate());
        chatHolder.name.setText(chat.getName());
    }


    @Override
    public int getItemCount() {
        return chats.size();
    }

    void updateData(List<ChatHistoryEntity> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    public class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView startDate;
        TextView messageCount;
        OnChatClickListener listener;

        ChatHolder(@NonNull View itemView, OnChatClickListener listener) {
            super(itemView);
            this.name = itemView.findViewById(R.id.chatName);
            this.startDate = itemView.findViewById(R.id.startDate);
            this.messageCount = itemView.findViewById(R.id.messageCount);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onChatClick(chats.get(getAdapterPosition()), view.getContext());
        }
    }


    public interface OnChatClickListener {
        void onChatClick(ChatHistoryEntity chat, Context context);
    }
}
