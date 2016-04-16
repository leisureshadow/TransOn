package com.ntu.transon.meeting_room;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ntu.transon.R;
import com.ntu.transon.meeting_room.Message;

import java.util.ArrayList;

/**
 * Created by Mars on 2014/12/27.
 */
public class MessageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Message> messages;
    private Activity activity;

    public MessageAdapter(Activity activity, ArrayList<Message> messages) {
        inflater = LayoutInflater.from(activity);
        this.messages = messages;
        this.activity = activity;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public Message getMessage(int position) {
        return messages.get(position);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, null);
        }

        Message message = messages.get(position);
        TextView mSpeaker = (TextView) convertView.findViewById(R.id.item_message_speaker);
        mSpeaker.setText(message.getSpeaker().getName());
        TextView mMessage = (TextView) convertView.findViewById(R.id.item_message_message);
        mMessage.setText(message.getMessage());
        TextView mTimestamp = (TextView) convertView.findViewById(R.id.item_message_timestamp);
        mTimestamp.setText(message.getTimestamp().toString());

        return convertView;
    }
}
