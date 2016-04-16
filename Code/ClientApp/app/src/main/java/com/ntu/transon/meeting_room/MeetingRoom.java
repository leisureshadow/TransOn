package com.ntu.transon.meeting_room;

import android.app.Activity;
import android.widget.Toast;

import com.ntu.transon.User;
import com.ntu.transon.UserAdapter;
import com.ntu.transon.activities.MeetingRoomActivity;

import java.util.ArrayList;

/**
 * Created by Mars on 2015/1/1.
 */
public class MeetingRoom extends Room {

    private UserAdapter accessResquestAdapter;

    private MeetingRoomActivity activity;

    public MeetingRoom(MeetingRoomActivity activity, MessageAdapter messageAdapter, MeetingInformation meetingInformation) {
        super(messageAdapter, meetingInformation, new MeetingParticipation());
        this.activity = activity;
        this.accessResquestAdapter = new UserAdapter(activity, new ArrayList<User>());
    }

    // TODO may have performance issue
    public void addMessage(Message message) {
        messages.add(message);
        sortMessages();
        notifyAdapter();
    }

    public UserAdapter getAccessResquestAdapter() {
        return accessResquestAdapter;
    }

    public Activity getMeetingRoomActivity() {
        return activity;
    }

    public void notifyIsAdmin(boolean isAdmin) {
        activity.notifyIsAdmin(isAdmin);
    }

    public void showToast(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
