package com.example.wifidirectchat.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifidirectchat.R;
import com.example.wifidirectchat.model.MessageEntity;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private static final int SENT = 0;
    private static final int RECEIVED = 1;


    private List<MessageEntity> messageEntities;
    private Map<MessageEntity,Boolean> dateVisible;

    public MessageListAdapter(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
        dateVisible = new HashMap<>();
        for (MessageEntity entity : messageEntities) {
            dateVisible.put(entity,false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageEntities.get(position).isSentByMe())
            return SENT;
        return RECEIVED;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutId = (i == SENT) ? R.layout.sent_message_holder : R.layout.received_message_holder;

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        MessageEntity m = messageEntities.get(i);
        messageViewHolder.text.setText(m.getMessage());
        messageViewHolder.date.setText(m.getDate().toString());
        messageViewHolder.setDateVisibility(dateVisible.get(m));
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        TextView date;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.messageText);
            date = itemView.findViewById(R.id.date);
            text.setOnClickListener(this);
        }

        public void setDateVisibility(Boolean p){
            if(p) {
                date.setVisibility(View.VISIBLE);
            }else{
                date.setVisibility(View.GONE);
            }
        }
        @Override
        public void onClick(View view) {
            MessageEntity entity = messageEntities.get(getAdapterPosition());
            if (dateVisible.get(entity)) {
                dateVisible.put(entity,false);
                date.setVisibility(View.GONE);
                return;
            }
            dateVisible.put(entity,true);
            date.setVisibility(View.VISIBLE);
        }
    }


    public void updateData(List<MessageEntity> lst) {
        messageEntities = lst;
        for(MessageEntity key : dateVisible.keySet()){
            if(!messageEntities.contains(key))
                dateVisible.remove(key);
        }
        for(MessageEntity key : messageEntities){
            if(!dateVisible.containsKey(key))
                dateVisible.put(key,false);
        }
        notifyDataSetChanged();
    }
}
