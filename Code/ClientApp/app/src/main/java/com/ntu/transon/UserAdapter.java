package com.ntu.transon;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ntu.transon.activities.MeetingRoomActivity;

import java.util.ArrayList;

/**
 * Created by Mars on 2014/12/21.
 */
public class UserAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<User> users;
    private Activity activity;

    public UserAdapter(Activity activity, ArrayList<User> users) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.users = users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDateSetChangedOnUiThread();
    }

    public User getUser(int position) {
        return users.get(position);
    }

    public void addUser(User user) {
        users.add(user);
        notifyDateSetChangedOnUiThread();
    }

    public void removeUser(int position) {
        users.remove(position);
        notifyDateSetChangedOnUiThread();
    }

    public void notifyDateSetChangedOnUiThread() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, null);
        }

        TextView mUsername = (TextView) convertView.findViewById(R.id.item_user_name);
        mUsername.setText(users.get(position).getName());

        return convertView;
    }
}
