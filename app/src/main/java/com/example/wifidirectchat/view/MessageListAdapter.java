package com.example.wifidirectchat.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wifidirectchat.R;
import com.example.wifidirectchat.model.MessageEntity;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private static final int SENT = 0;
    private static final int RECEIVED = 1;


    private List<MessageEntity> messageEntities;

    public MessageListAdapter(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
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
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        TextView date;
        boolean dateIsVisible;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.messageText);
            date = itemView.findViewById(R.id.date);
            date.setVisibility(View.GONE);
            dateIsVisible = false;
            text.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (dateIsVisible) {
                dateIsVisible = false;
                date.setVisibility(View.GONE);
                return;
            }
            dateIsVisible = true;
            date.setVisibility(View.VISIBLE);
        }
    }


    public void updateData(List<MessageEntity> lst) {
        messageEntities = lst;
        notifyDataSetChanged();
    }
}
